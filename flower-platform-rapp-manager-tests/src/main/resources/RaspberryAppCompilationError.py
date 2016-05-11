import time 
from datetime import datetime


while (True) :
	
	error line
	
	with open('temp.txt', 'w') as f :
		f.write("%s" % datetime.now().time())
	time.sleep(1)

