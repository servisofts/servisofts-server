# Soporte para Multipart/Form-Data en RequestParam

## Descripción

Se ha agregado soporte completo para recibir datos `multipart/form-data` en los controladores REST, incluyendo la capacidad de recibir archivos mediante la anotación `@RequestParam`.

## Nuevas Funcionalidades

### 1. Clase `MultipartFile`

Nueva clase para manejar archivos subidos:

```java
public class MultipartFile {
    public String getName();          // Nombre del campo
    public String getFileName();      // Nombre del archivo
    public String getContentType();   // Tipo MIME del archivo
    public byte[] getContent();       // Contenido del archivo en bytes
    public long getSize();            // Tamaño del archivo
    public boolean isEmpty();         // Verifica si el archivo está vacío
    public InputStream getInputStream(); // Obtiene un stream del contenido
    public void transferTo(File dest);   // Guarda el archivo en disco
}
```

### 2. Soporte en `@RequestParam`

La anotación `@RequestParam` ahora puede recibir:
- Archivos individuales: `MultipartFile`
- Múltiples archivos: `List<MultipartFile>`
- Campos de texto en multipart/form-data: `String`, `int`, `boolean`, etc.

## Ejemplos de Uso

### Ejemplo 1: Recibir un solo archivo

```java
@RestController("/api/files")
public class FileController {
    
    @PostMapping("/upload")
    public String uploadFile(
        @RequestParam(value = "file", required = true) MultipartFile file,
        @RequestParam(value = "description", required = false) String description
    ) {
        if (file.isEmpty()) {
            return "{\"error\": \"No file provided\"}";
        }
        
        // Guardar el archivo
        try {
            File destFile = new File("/uploads/" + file.getFileName());
            file.transferTo(destFile);
        } catch (IOException e) {
            return "{\"error\": \"Failed to save file\"}";
        }
        
        return "{\"success\": true, \"fileName\": \"" + file.getFileName() + "\"}";
    }
}
```

**Request con curl:**
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@/ruta/al/archivo.jpg" \
  -F "description=Mi archivo"
```

### Ejemplo 2: Recibir múltiples archivos

```java
@PostMapping("/upload-multiple")
public String uploadMultiple(
    @RequestParam(value = "files", required = true) List<MultipartFile> files,
    @RequestParam(value = "userId", required = true) String userId
) {
    for (MultipartFile file : files) {
        System.out.println("Archivo: " + file.getFileName() + 
                         " - Tamaño: " + file.getSize() + " bytes");
    }
    
    return "{\"success\": true, \"count\": " + files.size() + "}";
}
```

**Request con curl:**
```bash
curl -X POST http://localhost:8080/api/files/upload-multiple \
  -F "files=@/ruta/archivo1.jpg" \
  -F "files=@/ruta/archivo2.png" \
  -F "userId=123"
```

### Ejemplo 3: Mezcla de archivos y datos

```java
@PostMapping("/profile")
public String updateProfile(
    @RequestParam(value = "avatar", required = false) MultipartFile avatar,
    @RequestParam(value = "name", required = true) String name,
    @RequestParam(value = "email", required = true) String email,
    @RequestParam(value = "age", required = false) int age
) {
    String response = "{\"name\": \"" + name + "\", \"email\": \"" + email + "\"";
    
    if (avatar != null && !avatar.isEmpty()) {
        response += ", \"avatar\": \"" + avatar.getFileName() + "\"";
    }
    
    response += "}";
    return response;
}
```

**Request con curl:**
```bash
curl -X POST http://localhost:8080/api/files/profile \
  -F "avatar=@/ruta/avatar.jpg" \
  -F "name=Juan Pérez" \
  -F "email=juan@example.com" \
  -F "age=30"
```

### Ejemplo 4: Solo campos de texto (sin archivos)

```java
@PostMapping("/text-data")
public String saveTextData(
    @RequestParam(value = "title", required = true) String title,
    @RequestParam(value = "content", required = true) String content,
    @RequestParam(value = "published", required = false) boolean published
) {
    return "{\"title\": \"" + title + "\", \"published\": " + published + "}";
}
```

**Request con curl:**
```bash
curl -X POST http://localhost:8080/api/files/text-data \
  -F "title=Mi Artículo" \
  -F "content=Contenido del artículo" \
  -F "published=true"
```

## Tipos de Datos Soportados

Los parámetros pueden ser de los siguientes tipos:

### Para archivos:
- `MultipartFile` - Un solo archivo
- `List<MultipartFile>` - Múltiples archivos con el mismo nombre de campo

### Para campos de texto:
- `String` - Texto
- `int`, `long`, `double` - Números
- `boolean` - Booleanos
- `Date` - Fechas
- `BigDecimal`, `BigInteger` - Números grandes
- Cualquier otro tipo soportado por `parseValue()`

## Características Técnicas

### Detección Automática
El sistema detecta automáticamente cuando el `Content-Type` es `multipart/form-data` y procesa los datos en consecuencia.

### Manejo de Errores
- Si un parámetro requerido no está presente, se lanza un `HttpException` con código `BAD_REQUEST`
- Si hay un error al parsear el multipart, se lanza un `HttpException` con el mensaje de error

### Compatibilidad
- Compatible con los parámetros de URL query tradicionales
- Si se envía multipart/form-data, tiene prioridad sobre los query parameters
- Los campos de texto en multipart se parsean automáticamente al tipo esperado

## Notas Importantes

1. **Content-Type**: El cliente debe enviar el header `Content-Type: multipart/form-data` con el boundary correcto
2. **Múltiples archivos**: Para enviar múltiples archivos, usar el mismo nombre de campo múltiples veces
3. **Campos requeridos**: Usar `required = true` en `@RequestParam` para hacer obligatorio un parámetro
4. **Tamaño de archivos**: El sistema usa `DiskFileItemFactory` de Apache Commons FileUpload, que maneja archivos grandes eficientemente

## Dependencias

Asegúrate de tener estas dependencias en tu proyecto:
```xml
<dependency>
    <groupId>commons-fileupload</groupId>
    <artifactId>commons-fileupload</artifactId>
    <version>1.4</version>
</dependency>
```

## Testing

Puedes probar los endpoints usando:
- **curl**: Como se muestra en los ejemplos
- **Postman**: En la pestaña "Body" selecciona "form-data"
- **Navegador**: Usando un formulario HTML con `enctype="multipart/form-data"`

Ejemplo de formulario HTML:
```html
<form action="http://localhost:8080/api/files/upload" method="POST" enctype="multipart/form-data">
    <input type="file" name="file" required>
    <input type="text" name="description" placeholder="Descripción">
    <button type="submit">Subir</button>
</form>
```
