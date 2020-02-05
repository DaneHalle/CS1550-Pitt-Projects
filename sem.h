//FILE PATH: FILE PATH: ../linux-2.6.23.1/include/sem.h
struct cs1550_sem //Semaphore struct with value and pointer to a priority queue
{
    int value;
    struct cs1550_queue *head; //Item to be popped from queue
};

struct cs1550_queue //Priority Queue struct that uses a linked list implementation
{
    int pri;
    struct cs1550_queue *next;
    struct task_struct *task;
};