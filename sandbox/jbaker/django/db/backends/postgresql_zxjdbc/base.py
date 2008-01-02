"""
PostgreSQL database backend for Django.

Requires zxJDBC + PostgreSQL JDBC Driver
"""
import datetime
import sys

from django.db.backends import BaseDatabaseWrapper, BaseDatabaseFeatures
from django.db.backends.postgresql.operations import DatabaseOperations
from django.utils.functional import Promise
from django.utils.encoding import force_unicode

try:
    from com.ziclix.python.sql import zxJDBC as Database
    from com.ziclix.python.sql import FilterDataHandler, JDBC30DataHandler
    from java.sql import Timestamp, Date, Time, Types
    from java.util import Calendar, GregorianCalendar
    from org.python.core import Py
except ImportError, e:
    from django.core.exceptions import ImproperlyConfigured
    raise ImproperlyConfigured("Error loading zxJDBC module: %s" % e)


DatabaseError = Database.DatabaseError
IntegrityError = Database.IntegrityError

class DatabaseFeatures(BaseDatabaseFeatures):
    needs_datetime_string_cast = False

class DatabaseWrapper(BaseDatabaseWrapper):
    features = DatabaseFeatures()
    ops = DatabaseOperations()
    operators = {
        'exact': '= %s',
        'iexact': 'ILIKE %s',
        'contains': 'LIKE %s',
        'icontains': 'ILIKE %s',
        'regex': '~ %s',
        'iregex': '~* %s',
        'gt': '> %s',
        'gte': '>= %s',
        'lt': '< %s',
        'lte': '<= %s',
        'startswith': 'LIKE %s',
        'endswith': 'LIKE %s',
        'istartswith': 'ILIKE %s',
        'iendswith': 'ILIKE %s',
    }

    def _cursor(self, settings):
        set_tz = False
        if self.connection is None:
            set_tz = True
            if settings.DATABASE_NAME == '':
                from django.core.exceptions import ImproperlyConfigured
                raise ImproperlyConfigured("You need to specify DATABASE_NAME in your Django settings file.")
            host = settings.DATABASE_HOST or 'localhost'
            port = settings.DATABASE_PORT and (':' + settings.DATABASE_PORT) or ''
            conn_string = "jdbc:postgresql://%s%s/%s" % (host, port,
                                                         settings.DATABASE_NAME)
            self.connection = Database.connect(conn_string,
                                               settings.DATABASE_USER,
                                               settings.DATABASE_PASSWORD,
                                               'org.postgresql.Driver',
                                               **self.options)
            # make transactions transparent to all cursors
            from java.sql import Connection
            jdbc_conn = self.connection.__connection__
            jdbc_conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED)
        real_cursor = self.connection.cursor()
        # setup the JDBC3 DataHandler and our data handler
        real_cursor.datahandler = JDBC30DataHandler(DjangoDataHandler(real_cursor.datahandler))
        cursor = CursorWrapper(real_cursor)
        if set_tz:
            # XXX: settings.TIME_ZONE could inject SQL
            #      Does it matter? If someone can edit settings.py, it already
            #      have the user and password!
            cursor.execute("SET TIME ZONE %s" %
                           self.ops.quote_name(settings.TIME_ZONE))

        return cursor

class DjangoDataHandler(FilterDataHandler):
    def getPyObject(self, set, col, datatype):
        "Convert Java types into Python ones"
        if datatype in (Types.VARCHAR, Types.CHAR):
            return Py.newUnicode(set.getString(col))
        elif datatype == Types.TIMESTAMP:
            # Convert java.sql.TimeStamp into datetime
            cal = GregorianCalendar()
            cal.time = set.getTimestamp(col)
            return datetime.datetime(cal.get(Calendar.YEAR),
                                     cal.get(Calendar.MONTH) + 1,
                                     cal.get(Calendar.DAY_OF_MONTH),
                                     cal.get(Calendar.HOUR_OF_DAY),
                                     cal.get(Calendar.MINUTE),
                                     cal.get(Calendar.SECOND),
                                     cal.get(Calendar.MILLISECOND) * 1000)
        elif datatype == Types.TIME:
            # Convert java.sql.Time into time
            cal = GregorianCalendar()
            cal.time = set.getTime(col)
            return datetime.time(cal.get(Calendar.HOUR_OF_DAY),
                                 cal.get(Calendar.MINUTE),
                                 cal.get(Calendar.SECOND),
                                 cal.get(Calendar.MILLISECOND) * 1000)
        elif datatype == Types.DATE:
            # Convert java.sql.Date into datetime
            cal = GregorianCalendar()
            cal.time = set.getDate(col)
            return datetime.date(cal.get(Calendar.YEAR),
                                 cal.get(Calendar.MONTH) + 1,
                                 cal.get(Calendar.DAY_OF_MONTH))
        else:
            return FilterDataHandler.getPyObject(self, set, col, datatype)

    def _hour_minute_second_micro(self, s):
        hour, minute, second_and_microsecond = s.split(':')
        if '.' in second_and_microsecond:
            second, microsecond = second_and_microsecond.split('.')
        else:
            second, microsecond = second_and_microsecond, 0
        return int(hour), int(minute), int(second), int(microsecond)

    def _year_month_day(self, s):
        return [int(x) for x in s.split('-')]

    def setJDBCObject(self, stmt, index, obj, datatype=None) :
        "Convert Django-models types into Java ones"
        if datatype is None:
            FilterDataHandler.setJDBCObject(self, stmt, index, obj)
            return
        if datatype == Types.TIMESTAMP and isinstance(obj, basestring):
            # Convert string into java.sql.Timestamp
            # The string is generated by Django using datetime.__str__ ,
            # so the format is year-month-day hour:minute:second.microsecond
            d, t = obj.split(' ')
            hour, minute, second, microsecond = self._hour_minute_second_micro(t)
            year, month, day = self._year_month_day(d)
            # FIXME: This ignores microseconds
            obj = Database.Timestamp(year, month, day, hour, minute, second)   # Database is an alias for zxJDBC
        elif datatype == Types.TIME and isinstance(obj, basestring):
            # Convert string into java.sql.Time
            hour, minute, second, microsecond = self._hour_minute_second_micro(obj)
            # FIXME: This ignores microseconds
            obj = Database.Time(int(hour), int(minute), int(second))
        elif datatype == Types.DATE and isinstance(obj, basestring):
            year, month, day = self._year_month_day(obj)
            obj = Database.Date(year, month, day)
        FilterDataHandler.setJDBCObject(self, stmt, index, obj, datatype)


class CursorWrapper(object):
    """
    A simple wrapper to do the "%s" -> "?" replacement before running zxJDBC's
    execute or executemany
    """
    def __init__(self, cursor):
        self.cursor = cursor
    def execute(self, sql, params=()):
        self.cursor.execute(sql.replace('%s', '?'), params)
    def executemany(self, sql, param_list):
        self.cursor.executemany(sql.replace('%s', '?'), param_list)
    def __getattr__(self, attr):
        if attr in self.__dict__:
            return self.__dict__[attr]
        else:
            return getattr(self.cursor, attr)

