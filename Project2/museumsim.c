// ./museumsim -m 1 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed1.txt
// ./museumsim -m 10 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed2.txt
// ./museumsim -m 10 -k 1 -pv 50 -dv 1 -sv 1 -pg 50 -dg 1 -sg 2 > passed3.txt
// ./museumsim -m 50 -k 5 -pv 100 -dv 1 -sv 10 -pg 0 -dg 3 -sg 20 > passed4.txt
// ./museumsim -m 50 -k 5 -pv 0 -dv 1 -sv 10 -pg 0 -dg 12 -sg 20 > failed5.txt
// ./museumsim -m 10 -k 1 -pv 0 -dv 1 -sv 1 -pg 0 -dg 1 -sg 2 > passed6.txt
// ./museumsim -m 2 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed7.txt
// ./museumsim -m 3 -k 1 -pv 0 -dv 1 -sv 1 -pg 0 -dg 1 -sg 2 > passed8.txt
// ./museumsim -m 4 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed9.txt
// ./museumsim -m 5 -k 1 -pv 50 -dv 1 -sv 1 -pg 50 -dg 1 -sg 2 > passed10.txt
// ./museumsim -m 6 -k 1 -pv 0 -dv 1 -sv 1 -pg 0 -dg 1 -sg 2 > passed11.txt
// ./museumsim -m 7 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed12.txt
// ./museumsim -m 8 -k 1 -pv 0 -dv 1 -sv 1 -pg 0 -dg 1 -sg 2 > passed13.txt
// ./museumsim -m 9 -k 1 -pv 100 -dv 1 -sv 1 -pg 100 -dg 1 -sg 2 > passed14.txt
// ./museumsim -m 11 -k 2 -pv 100 -dv 1 -sv 1 -pg 0 -dg 3 -sg 2 > failed15.txt
// ./museumsim -m 22 -k 3 -pv 100 -dv 1 -sv 1 -pg 0 -dg 3 -sg 2 > passed16.txt
// ./museumsim -m 32 -k 4 -pv 100 -dv 0 -sv 1 -pg 0 -dg 3 -sg 2 > passed17.txt

#include "sem.h"
#include "unistd.h"
#include <sys/mman.h>
#include <time.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

int startTime; //Global startTime 

void down(struct cs1550_sem *sem) //syscall provided for down
{
	syscall(__NR_cs1550_down, sem);
}

void up(struct cs1550_sem *sem) //syscall provided for up
{
	syscall(__NR_cs1550_up, sem);
}

int getTime() //Gets the current time - startTime 
{
	return getCur()-startTime;
}


int getCur() //helper function to get the current time
{
    struct timeval tv;
    gettimeofday(&tv,NULL);
    return (int)(tv.tv_sec+1e-6*tv.tv_usec);
}

typedef struct museumShared{ //Shared variable for process sync 
	struct cs1550_sem *lock; //Main lock that is used on every method
	struct cs1550_sem *wantToOpen; //If a guide arrives and can't open yet 
	struct cs1550_sem *wantToTour; //If a visitor arrives and can't tour yet
	struct cs1550_sem *lastVisitor; //If a visitor is last in museum to send home guides
	int *guidesLeft; //Amount of unused guides left
	int *guidesInside; //Amount of guides within the museum (Won't be > than 2)
	int *visitLeft; //Amount of untoured visitors left
	int *visitInside; //Amount of visitors within the museum
	int *numViews; //Number of views. Sort of a counting variable
	int *numWaiting; //Number of visitors waiting to enter. 
	bool *museumOpen; //Is museum open
} museumShared;

/*
 * Takes in a shared variable and process id.
 * down() the lock and checks if the visitor can enter. 
 * If there are not enough resources for the visitor to ever enter, will exit.
 * If there are not enough resources for the visitor to enter now, will go to sleep.
 * If there are enough resources for the visitor to enter now, will exit method to tour.
 * At every exit, will up() the lock.
 */
void visitorArrives(museumShared *mShare, int id)
{
	down(mShare->lock);
	printf("Visitor %d arrives at time %d.\n", id, getTime());
	fflush(stdout);
	*(mShare->numWaiting)+=1;
	up(mShare->wantToOpen);
    if(!*(mShare->museumOpen) || (*(mShare->numViews)>=10 && *mShare->guidesLeft>=2)){
    	up(mShare->lock);
    		down(mShare->wantToTour);
    	down(mShare->lock); 
    }
    *(mShare->visitInside)+=1;
    *(mShare->numViews)+=1;
	up(mShare->lock);
}

/*
 * Takes in a shared variable and process id.
 * down() the lock and checks if the guide can start working.
 * If there are no visitors left to guide, will exit.
 * If there are 2 guides within the museum, will sleep until one leaves.
 * Checks to see if visitors are left again and then will exit to open.
 * At every exit, will up() lthe lock.
 */
void tourguideArrives(museumShared *mShare, int id)
{
	down(mShare->lock);
	printf("Tour guide %d arrives at time %d.\n", id, getTime());
	fflush(stdout);
    if(*(mShare->guidesInside)==2){
    	up(mShare->lock);
    		down(mShare->wantToOpen);
    	down(mShare->lock);
    }
	up(mShare->lock);
    *(mShare->guidesInside)+=1;
}

/*
 * Takes in a shared variable and process id.
 * Decreases the number waiting and then sleep(2).
 */
void tourMuseum(museumShared *mShare, int id)
{
	down(mShare->lock);
	printf("Visitor %d tours the museum at time %d.\n", id, getTime());
	fflush(stdout);
	*(mShare->numWaiting)-=1;
	up(mShare->lock);
	sleep(2);
}

/*
 * Takes in a shared variable and process id.
 * Sets museum to open and then wakes up every waiting process.
 */
void openMuseum(museumShared *mShare, int id)
{
	down(mShare->lock);
	printf("Tour guide %d opens the museum for tours at time %d.\n", id, getTime());
	fflush(stdout);
	*(mShare->museumOpen)=true;
	int i;
    for(i=0; i<*(mShare->numWaiting) && i<10; i++){
		up(mShare->wantToTour);
    }
	up(mShare->lock);
}

/*
 * Takes in a shared variable and process id.
 * Decreases both the visitors inside and left. 
 * Checks if process is the last visitor in the museum. 
 * If yes, checks if there are one or two guides waiting inside and up()s accordingly. 
 */
void visitorLeaves(museumShared *mShare, int id)
{
	down(mShare->lock);
	printf("Visitor %d leaves the museum at time %d.\n", id, getTime());
	fflush(stdout);
	*(mShare->visitInside)-=1;
    *(mShare->visitLeft)-=1;
    if(*(mShare->visitInside)==0 && *(mShare->guidesInside)>0){
    	if(*(mShare->guidesInside)==1){
    		*(mShare->numViews)-=10;
    		up(mShare->lastVisitor);
    	}else if(*(mShare->guidesInside)>1){
    		*(mShare->numViews)-=20;
    		up(mShare->lastVisitor);
    		up(mShare->lastVisitor);
    	}
    }
	up(mShare->lock);
}

/*
 * Takes in a shared variable and process id.
 * First checks if a visitor is still in the museum and sleeps if one is.
 * Decreases both guides left and inside. 
 * Checks if this was the last guide inside the museum and states that museum is empty. 
 * 
 */
void tourguideLeaves(museumShared *mShare, int id)
{
    if(*(mShare->visitInside)>0 || *(mShare->numViews)<1 || *(mShare->numWaiting)>1 || *(mShare->guidesInside)>1 || *(mShare->visitLeft)>0){
    	down(mShare->lastVisitor);
    }
	down(mShare->lock);
	printf("Tour guide %d leaves the museum at time %d.\n", id, getTime());
	fflush(stdout);
	*(mShare->guidesLeft)-=1;
	*(mShare->guidesInside)-=1;
    if(*(mShare->visitInside)==0 && *(mShare->guidesInside)==0){
        *(mShare->museumOpen)=false;
    	printf("The museum is now empty.\n");
    	fflush(stdout);
    }
	up(mShare->wantToOpen);
	up(mShare->lock);
}

/*
 * Takes in a shared variable and process id.
 * Handles the flow for guides.
 */
void guide_process(museumShared *mShare, int id)
{
	tourguideArrives(mShare, id);
	openMuseum(mShare, id);
	tourguideLeaves(mShare, id);
}

/*
 * Takes in a shared variable and process id.
 * Handles the flow for visitors.
 */
void visitor_process(museumShared *mShare, int id)
{	
	visitorArrives(mShare, id);
	tourMuseum(mShare, id);
	visitorLeaves(mShare, id);
}

/*
 * main
 * Must take all 16 arguments (<flag> <value>). Can be in any order.
 * Has no error checking if user gives multiple of the same flag.
 */
int main(int argc, char** argv)
{
	if(argc<16){
		printf("Not enough arguments. Format needs to be as follows:\n");
		fflush(stdout);
		printf("./museumsim -m <val> -k <val> -pv <val> -dv <val> -pg <val> -dg <val> -sv <val> -sg <val>\n");
		fflush(stdout);
		return 0;
	}else{
		int m, k, pv, dv, pg, dg, sv, sg=0;	 
	    int i;
		for(i=1; i<argc; i+=2){
			if(strcmp(argv[i], "-m")==0){
				m=atoi(argv[i+1]);
			}else if(strcmp(argv[i], "-k")==0){
				k=atoi(argv[i+1]);
			}else if(strcmp(argv[i], "-pv")==0){
				pv=atoi(argv[i+1]);
			}else if(strcmp(argv[i], "-dv")==0){
				dv=atoi(argv[i+1]);
			}else if(strcmp(argv[i], "-pg")==0){
				pg=atoi(argv[i+1]);
			}else if(strcmp(argv[i], "-dg")==0){
				dg=atoi(argv[i+1]);
			}else if(strcmp(argv[i], "-sv")==0){
				sv=atoi(argv[i+1]);
			}else if(strcmp(argv[i], "-sg")==0){
				sg=atoi(argv[i+1]);
			}
		}
		//The making of the shared variable for the processes. 
		void *ptr=mmap(NULL, sizeof(struct museumShared), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANONYMOUS, 0, 0);
		struct cs1550_sem *inlock=(struct cs1550_sem *)ptr;
		struct cs1550_sem *inToOpen=inlock+1;
		struct cs1550_sem *inToTour=inToOpen+1;
		struct cs1550_sem *inLast=inToTour+1;
		int *inGuideLeft=(int *)(inLast+1);
		int *inGuideInside=inGuideLeft+1;
		int *inVisitLeft=inGuideInside+1;
		int *inVisitInside=inVisitLeft+1;
		int *inNumViews=inVisitInside+1;
		int *inNumWaiting=inNumViews+1;
		bool *inMuseumOpen=(bool *)(inNumWaiting+1);
		inlock->value=1;	inToOpen->value=0;
		inToTour->value=0;	inLast->value=0;
		*inGuideLeft=k;		*inGuideInside=0;
		*inVisitLeft=m;		*inVisitInside=0;
		*inNumViews=0;		*inNumWaiting=0;
		*inMuseumOpen=false;
		museumShared mShare;
		mShare.lock=inlock;
		mShare.wantToOpen=inToOpen;
		mShare.wantToTour=inToTour;
		mShare.lastVisitor=inLast;
		mShare.guidesLeft=inGuideLeft;
		mShare.guidesInside=inGuideInside;
		mShare.visitLeft=inVisitLeft;
		mShare.visitInside=inVisitInside;
		mShare.numViews=inNumViews;
		mShare.numWaiting=inNumWaiting;
		mShare.museumOpen=inMuseumOpen;
		printf("The museum is now empty\n");
		fflush(stdout);
		startTime=getCur();
		int pid=fork();
		if(pid==0){ //Child process/Visitor Spawner
			srand(sv);
			int i;
			for(i=0;i<m; i++){
				int spawn=fork();
				if(spawn==0){
					visitor_process(&mShare, i);
					break;
				}else{
					if(i==m){
						int v;
						for(v=0; v<m; v++){
							wait(NULL);
						}
					}else{
						int burst=rand()%100+1<=pv;
						if(!burst){
							sleep(dv);
						}
					}
				}
			}
			for(i=0; i<m; i++){
				wait(NULL);
			}
		}else{ //Parent process/Guide Spawner
			int pid=fork();
			if(pid==0){
				srand(sg);
				int j;
				for(j=0; j<k; j++){
					int spawn=fork();
					if(spawn==0){
						guide_process(&mShare, j);
						break;
					}else{
						if(j==m){
							int g;
							for(g=0; g<k; g++){
								wait(NULL);
							}
						}else{
							int burst=rand()%100+1<=pg;
							if(!burst){
								sleep(dg);
							}
						}
					}
				}
			}else{
				wait(NULL);
				wait(NULL);
			}
		}
		return 0;
	}
}