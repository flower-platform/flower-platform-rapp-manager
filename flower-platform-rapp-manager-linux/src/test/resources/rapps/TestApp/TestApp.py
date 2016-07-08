import time
from datetime import datetime


while (True) :
	with open('temp.txt', 'w') as f :
		print("%s" % datetime.now().time());
		f.write("%s" % datetime.now().time())
	time.sleep(1)
