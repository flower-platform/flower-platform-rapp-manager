import time 
from datetime import datetime


while (True) :
	
	big error here
	
	with open('temp.txt', 'w') as f :
		f.write("%s" % datetime.now().time())
	time.sleep(1)

