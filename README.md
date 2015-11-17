# server

###TO RUN THE SERVER:

  1. git clone and cd into the folder 'Play'
  2. mvn install
  3. java -cp target/123-1.0-SNAPSHOT.jar MyServer
  
###TO TEST THE SERVER USING bmemcached:
  Please do not use auth to connect to the server, becuase it doesn't support authentication.
  Use:
  client = bmemcached.Client(('127.0.0.1',)) 
  to connect to the server locally.
  
###IMPLEMENTATION INTRO:
  1) The server supports memcached protocols on top of TCP. It supports only get and set request. 
  The key-pair value is restricted to String type.
  2) It is a single machine design for simplicity.
  3) A HashMap is used for key-value pair storage. It stores as much data as JVM memory limit allows. 
  Theoretically, a part of the data is not in physical memory if too much data is stored. 
  4) Concurrent Accesses are handled by second reader-writer algorithm. It allows multiple readers to read value and 
  any put(key, value) operation is atomic for readers and other writers. Writers always have priority over readers when
  accessing. Race condition happens when more than two readers or writers access the map. 
  There is no deadlock in the code because there exists no cycle in resource-request graph.
  
  ###Things to improve:
  1) To support large data storage, a multi-machine system is to be designed. We could use master-slave pattern where 
  one machine is used as master to store metadata of how key-value pairs are distributed across slave machines. The 
  metadata could be a sturcture of a two-level B+ tree where each leaf node stores a slave machine addresss. The B+ tree 
  are constructed by key value. Each slave machine stores key-value pair in a local hashmap.
  2) To allow faster performance for high concurrent accesses, especially when there are a large number of writes, 
  we should create our own version of hash map. The new hash map would have fine-grained lock control over the array A
  where key-value Entry is stored. We carefully pick a number N, where N is the number of locks in our hash map.
  So any index in array A is controled by lock[index % N]. This way, more concurrency is allowed. Also, reader-writer
  algorithm could be still used on this map.
  3) Tradeoffs: Probably we want to use our memcached in different scenarios. In some cases, read request number is very
  high but we have to update the cache eagerly when a write request arrives. Then reader-writer II algorithm as used in 
  the implementation is preferred to prevent writer starvation. Vice versa. 
  4) More Robustness: Add key and value length restriction and handle errors. For now, the server is dumb 
  on abnormally long key and value and does not handle any errors due to that.
  
