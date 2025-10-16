from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.handlers import FTPHandler
from pyftpdlib.servers import FTPServer
import os

# Tạo thư mục demo
os.makedirs("d:/ftps_demo", exist_ok=True)

# Authorizer
authorizer = DummyAuthorizer()
authorizer.add_user("demo", "demo123", "d:/ftps_demo", perm="elradfmwMT")

# Plain FTP Handler
handler = FTPHandler
handler.authorizer = authorizer

# Server
server = FTPServer(("127.0.0.1", 2121), handler)
print("Plain FTP Server started on 127.0.0.1:2121")
print("Username: demo, Password: demo123")
print("Directory: d:/ftps_demo")
print("Press Ctrl+C to stop")

server.serve_forever()