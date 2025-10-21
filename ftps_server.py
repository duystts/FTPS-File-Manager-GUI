from pyftpdlib.authorizers import DummyAuthorizer
from pyftpdlib.servers import FTPServer
import os

# Tạo thư mục demo
os.makedirs("demo_files", exist_ok=True)

# Authorizer
authorizer = DummyAuthorizer()
authorizer.add_user("demo", "demo123", "demo_files", perm="elradfmwMT")

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
server = FTPServer(("127.0.0.1", 990), handler)
print("FTPS Server started on 127.0.0.1:990")
print("Username: demo, Password: demo123")
print("Directory: demo_files")
if tls_enabled:
    print("TLS: Enabled (Explicit)")
else:
    print("TLS: Not available - running as Plain FTP")
print("Press Ctrl+C to stop")

server.serve_forever()