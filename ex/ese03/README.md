# Chat Peer-to-Peer in Java

Esercizio di programmazione di rete in Java.

L'obiettivo è realizzare una semplice chat in cui ogni nodo è sia server sia client.

Ogni nodo:
- ascolta connessioni in ingresso su una porta;
- conosce una lista di peer;
- può connettersi a un peer;
- può inviare e ricevere messaggi tramite socket.

## Come testarlo

Per testare la chat bisogna avviare almeno **due nodi**, perché ogni nodo rappresenta un programma diverso.

Ogni nodo deve avere:

- una porta locale su cui ascolta;
- almeno un peer a cui potersi collegare.

L’esercizio richiede infatti che ogni nodo sia sia server sia client. :contentReference[oaicite:0]{index=0}

---

## Test con due terminali

Supponiamo di avere due classi main diverse:

- `Main3333`
- `Main2222`

### Terminale 1

Avvia il nodo che ascolta sulla porta `3333`.

```bash
java ese03.src.Main3333 2222
```

Questo nodo conosce il peer:

```text
localhost:2222
```

### Terminale 2

Avvia il nodo che ascolta sulla porta `2222`.

```bash
java ese03.src.Main2222 3333
```

Questo nodo conosce il peer:

```text
localhost:3333
```

