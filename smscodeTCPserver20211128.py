import socket
import threading
import time
import inspect
import ctypes

def _async_raise(tid, exctype):
    tid = ctypes.c_long(tid)
    if not inspect.isclass(exctype):
        exctype = type(exctype)
    res = ctypes.pythonapi.PyThreadState_SetAsyncExc(tid, ctypes.py_object(exctype))
    if res == 0:
        raise ValueError("invalid thread id")
    elif res != 1:
        ctypes.pythonapi.PyThreadState_SetAsyncExc(tid, None)
        raise SystemError("PyThreadState_SetAsyncExc failed")

def stop_thread(thread):
    _async_raise(thread.ident, SystemExit)

class threadingstart(threading.Thread):
    def __init__(self, arg):
        super(threadingstart, self).__init__()

    def run(self):
        self.result = start()

    def get_result(self):
        return self.result

def start():
    tcp = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    tcp.connect(('1.1.1.1', 1))
    host = tcp.getsockname()[0]
    port = 8989
    buffsize = 2048
    ADDR = (host, port)
    print(ADDR)
    tcp = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    tcp.bind(ADDR)
    tcp.listen(3)
    while True:
        try:
            tcpClient, addr = tcp.accept()
            while True:
                data = tcpClient.recv(buffsize).decode()
                if not data:
                    tcpClient.close()
                    break
                return data.replace('\n', '')
        except Exception as e:
            tcpClient.close()
            return -1

def stopclient():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(('1.1.1.1', 1))
    host = s.getsockname()[0]
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    port = 8989
    s.connect((host, port))
    s.close()

def clickbutton(threadName, delay):
    count = 0
    while count < 5:
        time.sleep(delay)
        count += 1
        print("%s: %s" % (threadName, time.ctime(time.time())))

def main(sec):
    t1 = threadingstart(start)
    t2 = threading.Thread(target=clickbutton, args=('thread-2', 1))
    t1.start()
    t2.start()
    Starttime = time.time()
    while t1.isAlive() and time.time()-Starttime < sec:
        time.sleep(2)
        print(t1.isAlive())
        print(time.time()-Starttime)
    try:
        result = int(t1.get_result())
    except Exception as e:
        stop_thread(t1)
        stopclient()
        result = 0
    finally:
        return result

if __name__ == "__main__":
    s = main(10)
    if s==0:
        print('no sms')
    else:
        print(s)





