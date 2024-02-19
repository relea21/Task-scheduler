Relea Florin 334CA

    Universal Dispatcher - clasa care implementeaza design pattern-ul Singlenton
pentru multithreading. Crearea unei noi instante se face folosind synchronized
pentru a nu exista posibilitatea ca 2 thread-uri concurente sa creeze 2 instante
diferite. Metoda syncronized este precedata de o verificare, pentru a se optimiza
returnarea instantei in cazul in care aceasta a fost creeata precedent. Este importanta
resetarea instantei in cazul in care dispatcherul se schimba.

    MyHost
   -Are 2 variabile volatile (running, care specifica daca nodul executa taskuri,
stopExecute, care specifica daca trebuie ca un task sa isi opreasca executia pentru a fi
inlocuit cu altul)
   -PriorityBlockingQueue<Task> taskQueue - coada de prioritate pentru task-uri in care
implementez comparatorul specific printr-o clasa anonima.
   -Am folosit un semafor pentru a semnaliza prezenta unui nou task adaugat in nod