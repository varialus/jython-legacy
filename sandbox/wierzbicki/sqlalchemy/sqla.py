from sqlalchemy import *

db = create_engine('mysql://mydb:mydb@localhost/mydb')
#             'jdbc:mysql://mydb:mydb@localhost/mydb'
#jdbc:mysql://localhost:3306/MySql"
metadata = BoundMetaData(db)

users_table = Table('users', metadata, autoload=True)

s = users_table.select()
r = s.execute()
print r.fetchall()
