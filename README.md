# Middleware

## Para debuggear en vscode

- Instala Maven antes de comenzar con el debuggeo
```bash
    sudo apt install maven -Y
```
El setup es el siguiente:  
0. Hacer un export ```IMQ_HOME='<path a la raíz del proyecto>/lib/bin'```
1. Primero nos aseguramos de que el broker funciona correctamente y tenemos el usuario funcionando. 
    Para ello debemos garantizar permisos de acceso a ```lib/var/instances/imqbroker/etc/passwd``` para poder crear usuarios a través del comando:
    ```bash
        sudo ./imqusermgr add -u guest -p guest
    ```
    (sudo es sólo una precaución)
2. Lo siguiente será inicializar el broker, ```./imqbrokerd``` (como vamos a debuggear usar -tty es redundante, pero podemos usarlo si queremos)
3. Y a debuggear! (recuerda los breakpoints antes de los try/catch o saltará todo el código)