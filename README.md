ConsoleCollection
Aplicaciones Moviles

##Tema: Colección de consolas
Es una aplicación que permite al usuario crear, ver, editar y eliminar consolas de videojuegos que tenga en su colección. También puede editar sus datos y eliminar su cuenta.

##Funcionalidades:
•	Crear una lista de consolas de su colección personal
Después de registrarse e iniciar sesión, el usuario puede crear una tarjeta con los datos de su consola para insertarlo en una lista. Una vez creada la tarjeta, aparece en la actividad principal, donde el usuario puede hacer clic en la tarjeta para ver los detalles de la consola. Dentro de esta actividad, podrá editar los datos de la consola en cuestión o eliminarla de su lista.
•	Gestiona sus datos
Desde la actividad principal, el usuario puede cerrar sesión o editar sus datos, yendo a la actividad de ajustes. En este espacio podrá editar su nombre, dirección de correo electrónico y contraseña. Además, también puede eliminar su cuenta, eliminando así sus datos y todas las consolas asociadas a su nickname.

##Controles de Uso:
•	Para registrarse:
El usuario debe ingresar un correo electrónico válido para registrarse y este correo no se puede repetir. Lo mismo ocurre con el nickname. En este momento la contraseña no tiene ningún tipo de verificación, ni hasheo.
•	Al iniciar sesión:
El usuario inicia sesión con su nickname y contraseña, los cuales deben coincidir con los registrados en la base de datos.
•	Para agregar una nueva consola:
El usuario debe agregar una imagen al crear un nuevo registro de consola. No hay límite para la cantidad de caracteres que puede insertar en los inputs.
•	Para cambiar datos:
Al igual que con el registro, el usuario debe ingresar un correo electrónico válido si edita este campo. No puede cambiar su nickname, ya que es su identificación única.
•	Para borrar datos:
Ya sea para eliminar datos de la consola o de su propia cuenta, el usuario deberá confirmar esta acción antes de realizarla.

##Base de Datos:
Esta aplicación utiliza los servicios proporcionados por Firebase como base de datos.
•	Realtime Database:
Los usuarios registrados se almacenan en la colección “users”, mientras que las consolas se almacenan en “consoles”. Este último tiene un campo llamado ‘owner’ que almacena el nickname del usuario. Así, la actividad principal muestra todas las consolas en las que el usuario ha iniciado sesión según su nickname. El modelo creado para usuarios se llama HelperClass y el modelo para consolas se llama ItemClass.
•	Storage:
Se utiliza para almacenar imágenes cargadas por los usuarios al crear un nuevo registro de consola. La URL de la imagen se guarda en el campo ‘picture’ de “consoles” para mostrarse en la aplicación.
