from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.servers import FTPServer
import os

# Tạo thư mục demo
os.makedirs("d:/ftps_demo", exist_ok=True)

# Authorizer
authorizer = DummyAuthorizer()
authorizer.add_user("demo", "demo123", "d:/ftps_demo", perm="elradfmwMT")

# Try import TLS handler
try:
    from pyftpdlib.handlers import TLS_FTPHandler
    handler = TLS_FTPHandler
    handler.certfile = "server.pem"
    handler.tls_control_required = False
    handler.tls_data_required = False
    tls_enabled = True
except ImportError:
    from pyftpdlib.handlers import FTPHandler
    handler = FTPHandler
    tls_enabled = False

handler.authorizer = authorizer

# Server
server = FTPServer(("127.0.0.1", 2121), handler)
print("FTPS Server started on 127.0.0.1:2121")
print("Username: demo, Password: demo123")
print("Directory: d:/ftps_demo")
if tls_enabled:
    print("TLS: Enabled (Explicit)")
else:
    print("TLS: Not available - running as Plain FTP")
print("Press Ctrl+C to stop")

server.serve_forever()