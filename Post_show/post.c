#include <stdio.h>
#include <stdlib.h>
#define MAXLEN 80

int main(int argc, char **argv) {
    char* pRequestMethod;
    printf("Content-Type:text/html\n\n");
    setvbuf(stdin,NULL,_IONBF,0);
    pRequestMethod = getenv("REQUEST_METHOD");

    if(strcmp(pRequestMethod,"POST") == 0){
        printf("<TITLE>This is POST operation</TITLE>\n");
        char poststr[80];
        char *lenstr = getenv("CONTENT_LENGTH");
        if(lenstr == NULL){
            printf("<P>Post form error");
        }else{
            long len = atoi(lenstr);
            printf("<p>The data of POST is: ");
            fgets(poststr,len+1,stdin);
            printf("%s</p>\n",poststr);
        }
    }

    if(strcmp(pRequestMethod,"GET") == 0){
        printf("<TITLE>This is Get operation</TITLE>\n");
        char *qa;
        printf("<TITLE>The result of Get is:\n</TITLE>\n");
        qa = getenv("QUERY_STRING");
        printf("%s",qa);
    }
}