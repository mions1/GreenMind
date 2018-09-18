Se si vuole dare i permessi di esecuzione a init.sh: 'sudo chmod 777 init.sh'

Eseguendo tale script, esso farà:
1. installazione postgresql se non installato
2. settaggio password per utente postgres
3. creazione database GreenMind (crea solo il db, non lo schema, ci penserà il programmino java per quello)


Lanciare il programma greenmind.jar col comando 'java -jar greenmind.jar'

Si può lanciare con i seguenti argomenti (consiglio il 4. per maggior flessibilità):

1. 'java -jar greenmind.jar', in questo modo vengono create tabelle e quant'altro se non esistono con: nome_db=GreenMind, utente=postgres, psw=postgres
2. 'java -jar greenmind.jar reset', in questo modo viene eliminato tutto lo schema del db e ricreato sia se esiste che se non esiste. Le credenziali sono come sopra.
3. 'java -jar greenmind.jar <nome_db> <username> <psw>' come 1. ma con le credenziali passate
4. 'java -jar greenmind.jar reset <nome_db> <username> <psw>' come 2. ma con le credenziali passate

Prima di lanciarlo, quindi, c'è bisogno di aver creato un database, anche non definito, ci penserà il programma, all'avvio, a creare le tabelle e tutto il resto.

Nella cartella GreenMind è situtato il codice del programma nonche la documentazione.

Nella cartella presentazione è situata, intuitivamente, la presentazione del nostro progetto in pdf.

Per effettuare le prove, senza dover scrivere ogni volta i codici fiscali, utilizzare la stringa "io" nel caso di accesso come cliente, la stringa "cam" per accedere come cameriere e "di" per accedere come gestore. Questi codici verranno sostituiti dai codici fiscali delle persone già registrate come esempio nel db.

