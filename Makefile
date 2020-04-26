all: httpd

httpd: httpd.c
	gcc -W -Wall -o httpserver httpd.c -lpthread

clean:
	rm httpserver
