# ENTORNO MACVLAN DOCKER


1. Instalar ubuntu 22.04.
    ```bash
   
   ```
3. Crear el usuario `servisofts` con el directorio home `/home/servisofts/`.
   ```bash
   
   ```
4. Asignar el rol `SUDO` a el usuario servisofts.
    ```bash
   
   ```
5. Creamos el directorio `/home/servisofts/servicios/`.
    ```bash
   
   ```
6. Instalamos el `Docker` y el `Docker Compose`.
    ```bash
   
   ```
7. Creamos un docker network macvlan con la ip que estaran alojados los servicios con el nombre servisofts.
    ```bash
    parent="em1" # La interface de RED de la pc que aloja la red.
    gateway="192.168.1.1" # La ip del router que vamos a conectar.
    subnet="192.168.5.0/21" # la mascara de red que usamos.
    iprange="192.168.5.100/21" # el rango para el DHCP.
    
    docker network create -d macvlan --subnet=$subnet --ip-range=$iprange --gateway=$gateway -o parent=$parent servisofts
   ```
8. Copiamos los entornos e iniciamos con el comando `./servisofts.sh up`.
    ```bash
   
   ```

### COMMANDS
-  `up` Inicia el entorno con sus logs en consola.
-  `up -d` Inicia el entorno en background.
-  `down`: Detiene el entorno.