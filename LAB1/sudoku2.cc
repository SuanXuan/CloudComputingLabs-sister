#include <assert.h>
#include <pthread.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/time.h>
#define _GUN_SOURCE
#include <sched.h>


#include "sudoku.h"

int GetCpuCount()
{
  return (int)sysconf(_SC_NPROCESSORS_ONLN);
}



int64_t now()
{
  struct timeval tv;
  gettimeofday(&tv, NULL);
  return tv.tv_sec * 1000000 + tv.tv_usec;
}

pthread_mutex_t lock; 
FILE* fp;


void* threadfunc(void *num)
{
        printf("%d num\n",num);
	char puzzle[128];
	bool flag=true;
	while(flag)
	{
                //printf("%d\n",flag);
		pthread_mutex_lock(&lock);
		if(fgets(puzzle,sizeof puzzle,fp)!=NULL)
		{
			pthread_mutex_unlock(&lock);
                        usleep(100);
			input(puzzle);
                        init_cache();
                        //printf("%s",puzzle);
			if(solve_sudoku_dancing_links(0))
			{
				if(!solve_sudoku_dancing_links(0)) assert(0);
				//printf("success\n");
			}
			else
			printf("fail\n");
			flag=true;
		}
		else
		{
                        printf("error\n");
			pthread_mutex_unlock(&lock);
			flag=false;
		}
	}
 } 

int main(int argc, char* argv[])
{
  int cpu_num=0;
  cpu_num =GetCpuCount();
  printf("The number of cpu is %d\n",cpu_num);

  init_neighbors();
  pthread_mutex_init(&lock,NULL);

  fp = fopen(argv[1], "r");
  //char puzzle[128];
  //int total_solved = 0;
  //int total = 0;
  //bool (*solve)(int) = solve_sudoku_basic;
  //if (argv[2] != NULL)
    //if (argv[2][0] == 'a')
      //solve = solve_sudoku_min_arity;
    //else if (argv[2][0] == 'c')
      //solve = solve_sudoku_min_arity_cache;
    //else if (argv[2][0] == 'd')
      //solve = solve_sudoku_dancing_links;
  int64_t start = now();

  pthread_t thread1,thread2;
  pthread_attr_t attr1,attr2;
  
  pthread_attr_init(&attr1);
  pthread_attr_init(&attr2);

  cpu_set_t cpu_info;
  CPU_ZERO(&cpu_info);
  CPU_SET(0,&cpu_info);
  if (0!=pthread_attr_setaffinity_np(&attr1, sizeof(cpu_set_t), &cpu_info))
  {
    printf("set affinity failed");
        
   }
   CPU_ZERO(&cpu_info);
   CPU_SET(1, &cpu_info);
    if (0!=pthread_attr_setaffinity_np(&attr2, sizeof(cpu_set_t), &cpu_info))
    {
        printf("set affinity failed");
    }
  
  void* status1;
  void* status2;
  //void* status3;

  pthread_create(&thread1,&attr1,threadfunc,NULL);

  pthread_create(&thread2,&attr2,threadfunc,NULL); 
  //pthread_create(&thread3,NULL,threadfunc,NULL);

  pthread_join(thread1,&status1);
  pthread_join(thread2,&status2);
  //pthread_join(thread2,&status3);
  //while (fgets(puzzle, sizeof puzzle, fp) != NULL) {
    //if (strlen(puzzle) >= N) {
      //++total;
      //input(puzzle);
      //init_cache();
      //if (solve_sudoku_min_arity_cache(0)) {
      //if (solve_sudoku_min_arity(0))
      //if (solve_sudoku_basic(0)) {
      //if (solve(0)) {
        //++total_solved;
        //if (!solved())
        //  assert(0);
      //}
      //else {
       // printf("No: %s", puzzle);
     // }
    //}
 //}
  int64_t end = now();
  double sec = (end-start)/1000000.0;
  //printf("%f sec %f ms each %d\n", sec, 1000*sec/total, total_solved);
  printf("%f sec ", sec);
  return 0;
}


