from sqlalchemy import create_engine, Table, Column, Integer, String, MetaData, ForeignKey

db = create_engine('mysql://mydb:mydb@localhost/mydb')
#             'jdbc:mysql://mydb:mydb@localhost/mydb'
#jdbc:mysql://localhost:3306/MySql"
metadata = MetaData(db)
users = Table('users', metadata,
        Column('id', Integer, primary_key=True),
        Column('name', String(40)),
        Column('fullname', String(100)),
        )

addresses = Table('addresses', metadata, 
        Column('id', Integer, primary_key=True),
        Column('user_id', None, ForeignKey('users.id')),
        Column('email_address', String(50), nullable=False)
        )
metadata.create_all()
ins = users.insert(values={'name':'jack', 'fullname':'Jack Jones'})
conn = db.connect()
result = conn.execute(ins)
s = users.select()
r = s.execute()
print r.fetchall()
