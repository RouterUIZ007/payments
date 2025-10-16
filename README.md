# Rest API payments

> README para configurar y ejecutar el proyecto localmente.

### Características del proyecto

- **Spring boot**: version 3.5.6.
- **API RESTful**: Endpoints HTTP.
- **JPA**: Spring Data JPA y H2.
- **Spring Security**: Seguridad con autenticación JWT.
- **Data Base**: MySQL.

### Requisitos

- **Java 17** o superior.
- **Maven**.
- **IDE Recomendado**: IntelliJ IDEA.
- **.env**: Archivo para las variables de entorno.

### Crear BD

Entramos a nuestro ODBC y creamos un nuevo ***Schema*** con el nombre que allamos ocupado en
***DBNAME nombre de la base de datos***

### Configuracion .env

Se debe crear un archivo en la raiz de la carpeta
con el nombre `.env`

```
payments/
    └── .env
    .
    .   resto de carpetas y archivos
    .
```

el contenido sera el siguiente

en `<TITLE>` se debera templazar por los valores correspondientes

```
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_USERNAME=<USERNAME del USER de la base de datos>
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/<USERNAME de la base de datos>?createDatabaseIfNotExist=true&serverTimezone=GMT-6
SPRING_DATASOURCE_PASSWORD=<PASSWORD del USER de la base de datos>

# MYSQL ENVIRONMENT VARIABLES
MYSQL_DATABASE=<DBNAME nombre de la base de datos>
MYSQL_ROOT_PASSWORD=<USERNAME del USER de la base de datos>
MYSQL_PASSWORD=<PASSWORD del USER de la base de datos>

# JWT Configuration
JWT_SECRET=<JWTSECRET secret para el TOKEN JTW>
JWT_EXPIRATION=<EXPIRATION tiempo de expiracion del TOKEN JTW>

# LOGS
LOG_PATTERN="%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
LOG_FILE_NAME=logs/<nombre de archvio que contendra los LOGS>.txt
```

### Configuración de Ejecución/Depuración en IntelliJ IDEA

Para configurar la ejecución de la aplicación, sigue estos pasos:

1. Abre **IntelliJ IDEA** y selecciona el proyecto para abirir.
2. Dirígete a la sección **Run > Edit Configurations...** en el menú superior.
3. En el panel de configuraciones, selecciona o crea una nueva configuración de tipo **Application**.
4. En el campo **Name**, escribe el nombre de la aplicación que deseas ejecutar, por ejemplo, `PaymentsApplication`.
5. En la sección **Build and Run**, selecciona el **SDK** correspondiente para el proyecto. Asegúrate de que esté
   configurado mayor a **Java 17 SDK**.
6. En el cuadro de diálogo de **Environment Variables**, seleciona el icono `📁` y selecionar el .env que se creo.
7. Haz clic en **OK** o **Apply** para guardar los cambios.

***Ejecución***: Ahora ya nada mas ejecutamos **Run > Run 'PaymentsApplication'...** y se levantara el servidor listo
para probar.

### Importar collection a Postman

En este caso vamos a importar el archivo **payment.postman_collection.json**

1. Abre **Postman** en tu máquina.
2. En la interfaz de Postman, haz clic en el botón **Import** en la parte superior izquierda.
3. En el cuadro de diálogo que aparece, selecciona la opción **Upload Files**.
4. Navega hasta donde descargaste el archivo `payment.postman_collection.json` y selecciónalo. previamente descargado
5. Haz clic en **Open** para importar la colección.
6. Configuramos **Variables in request**,
7. Asiganmos el **API URL** de nuestra servidor en **SpringBoot**, por ejemplo `http://localhost:8080`

### Ejecutar las solicitudes

Una vez que hayas importado la colección y configurado cualquier variable de entorno, puedes comenzar a hacer peticiones
a tu API desde Postman:

### authenticate

**GET**
> {{SpringBoot}}/api/v1/auth/authenticate

Body, por ejemplo

```json
{
  "username": "abi",
  "password": "123456"
}
```

### Creación de Pago Referenciado

**POST**
> {{SpringBoot}}/api/v1/payment

Body: Se agregan los datos requeridos por ejemplo

```json
{
  "externalId": "123",
  "amount": 100.00,
  "description": "Payment description",
  "dueDate": "2025-10-15 00:00:00",
  "callbackURL": "https://myurl/callback"
}
```

### Consulta de Pago:

**GET**
> {{SpringBoot}}/api/v1/payment/{reference}/{paymentId}

Se coloca los valores {reference} y {paymentId} que se desena buscar

Body: no requiere

### Listado de Pagos

**GET**
> {{SpringBoot}}/api/v1/search

Params:

| **Campo**             | **Valor**             |             |
|-----------------------|-----------------------|-------------|
| **startCreationDate** | `2025-01-01 23:59:59` | `Requerido` |
| **endCreationDate**   | `2025-12-31 23:59:59` | `Requerido` |
| **startPaymentDate**  | `2025-01-01 23:59:59` | `Requerido` |
| **endPaymentDate**    | `2025-12-31 23:59:59` | `Requerido` |
| **status**            | `01`                  | `Requerido` |
| **paginate**          | `10`                  |             |
| **page**              | `0`                   |             |

Body: no requiere


### Cancelación de Pago

**POST**
> {{SpringBoot}}/api/v1/cancel

Body: Se agregan los datos requeridos por ejemplo

```json
{
   "reference": "B5C2E1424B7F411DA99EB9F6BFB6DC",
   "status": "03",
   "updateDescription": "Mensaje de cancelacion"
}
```