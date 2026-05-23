package Servisofts.http;

// import Servisofts.JSON;
import Servisofts.http.Exception.HttpCodeException;
import Servisofts.http.Exception.HttpException;
import Servisofts.http.annotation.DeleteMapping;
import Servisofts.http.annotation.GetMapping;
import Servisofts.http.annotation.PathVariable;
import Servisofts.http.annotation.PostMapping;
import Servisofts.http.annotation.PutMapping;
import Servisofts.http.annotation.RequestBody;
import Servisofts.http.annotation.RequestHeader;
import Servisofts.http.annotation.RequestParam;

// import Servisofts.mediator.Request;
// import Servisofts.swagger.parts.Path;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jboss.com.sun.net.httpserver.HttpExchange;

public class Action {

  enum ActionType {
    GET,
    POST,
    PUT,
    DELETE,
  }

  private ActionType type;
  private Method method;
  private String route;
  private ArrayList<String> params;

  public Action(Method method) throws HttpCodeException {
    this.method = method;
    Annotation annotation = method.getAnnotation(GetMapping.class);
    if (annotation instanceof GetMapping) {
      GetMapping customAnnotation = (GetMapping) annotation;
      this.route = createRoute(customAnnotation.value());
      this.type = ActionType.GET;
      return;
    }
    annotation = method.getAnnotation(PostMapping.class);
    if (annotation instanceof PostMapping) {
      PostMapping customAnnotation = (PostMapping) annotation;
      this.route = createRoute(customAnnotation.value());
      this.type = ActionType.POST;
      return;
    }
    annotation = method.getAnnotation(PutMapping.class);
    if (annotation instanceof PutMapping) {
      PutMapping customAnnotation = (PutMapping) annotation;
      this.route = createRoute(customAnnotation.value());
      this.type = ActionType.PUT;
      return;
    }
    annotation = method.getAnnotation(DeleteMapping.class);
    if (annotation instanceof DeleteMapping) {
      DeleteMapping customAnnotation = (DeleteMapping) annotation;
      this.route = createRoute(customAnnotation.value());
      this.type = ActionType.DELETE;
      return;
    }
    throw new HttpCodeException(
        "El metodo no tiene la anotacion GetMapping o PostMapping");
  }

  private static final Pattern p = Pattern.compile("\\{(.*?)\\}");

  public String createRoute(String route) {
    Matcher m = p.matcher(route);
    this.params = new ArrayList<>();
    while (m.find()) {
      String param = m.group(1);
      this.params.add(param);
    }
    return route;
  }

  public boolean equal(String method, String _route) {
    if (this.type != ActionType.valueOf(method)) {
      return false;
    }
    
    // Soporte para wildcard /** que permite cualquier ruta después
    if (this.route.endsWith("/**")) {
      String baseRoute = this.route.substring(0, this.route.length() - 3);
      return _route.startsWith(baseRoute) || _route.equals(baseRoute);
    }
    
    if (this.params.size() > 0) {
      String[] r_route = _route.split("/");
      String[] m_route = this.route.split("/");
      if (r_route.length != m_route.length) {
        return false;
      }
      for (int i = 0; i < m_route.length; i++) {
        if (m_route[i].startsWith("{") && m_route[i].endsWith("}")) {
          continue;
        }
        if (!m_route[i].equals(r_route[i])) {
          return false;
        }
      }
    } else {
      if (!this.route.equals(_route)) {
        return false;
      }
    }

    return true;
  }

  // instance es el Controller
  public void onMessage(
      HttpExchange t,
      Response response,
      String path,
      String data,
      byte[] bodyBytes,
      Object instance) throws HttpException {
    Parameter[] parameters = this.method.getParameters();
    Map<String, String> path_params = queryToMap(t.getRequestURI().getQuery());
    Map<String, Object> multipart_params = null;
    
    // Detectar si es multipart/form-data
    String contentType = t.getRequestHeaders().getFirst("Content-Type");
    System.out.println("Content-Type: " + contentType);
    if (contentType != null && contentType.toLowerCase().startsWith("multipart/form-data")) {
      multipart_params = parseMultipartFormData(t, bodyBytes);
      System.out.println("Multipart parsed successfully, params: " + (multipart_params != null ? multipart_params.keySet() : "null"));
    }
    
    // Class[] paramTypes = this.method.getParameterTypes();
    ArrayList<Object> values = new ArrayList<Object>();
    int i_p_v = -1;

    String[] arrp = this.route.split("/");
    ArrayList<String> lis = new ArrayList<>();
    for (String s : arrp) {
      lis.add(s);
    }

    for (Parameter parameter : parameters) {
      Object value = null;

      if(parameter.getType() == HttpExchange.class){
        values.add(t);
        continue;
      }
      Annotation annotation = parameter.getAnnotation(PathVariable.class);
      if (annotation instanceof PathVariable) {
        i_p_v++;

        int i = lis.indexOf("{" + this.params.get(i_p_v) + "}");
        if (i == -1) {
          throw new HttpException(Status.BAD_REQUEST,
              "Request param not found " + this.params.get(i_p_v));
        }
        value = path.split("/")[i];
        values.add(parseValue(value, parameter.getType()));
        continue;
      }
      annotation = parameter.getAnnotation(RequestBody.class);
      if (annotation instanceof RequestBody) {
        values.add(parseValue(data, parameter.getType()));
        continue;
      }
   
      annotation = parameter.getAnnotation(RequestParam.class);
      if (annotation instanceof RequestParam) {
        RequestParam anot = (RequestParam) annotation;
        String name = anot.value();
        boolean required = anot.required();

        System.out.println("Looking for RequestParam: " + name + " (required=" + required + ", type=" + parameter.getType().getSimpleName() + ")");

        Object paramValue = null;
        boolean found = false;

        // Primero verificar en multipart si está disponible
        if (multipart_params != null) {
          System.out.println("  Checking in multipart_params...");
          if (multipart_params.containsKey(name)) {
            found = true;
            Object multipartValue = multipart_params.get(name);
            System.out.println("  Found in multipart: " + multipartValue);
            
            // Si el tipo esperado es MultipartFile o List<MultipartFile>
            if (parameter.getType() == MultipartFile.class) {
              if (multipartValue instanceof MultipartFile) {
                paramValue = multipartValue;
              } else if (multipartValue instanceof List) {
                List<?> list = (List<?>) multipartValue;
                if (!list.isEmpty()) {
                  paramValue = list.get(0);
                }
              }
            } else if (parameter.getType() == List.class) {
              paramValue = multipartValue;
            } else {
              // Es un campo de texto normal en multipart
              paramValue = parseValue(multipartValue, parameter.getType());
            }
          } else {
            System.out.println("  NOT found in multipart_params");
          }
        } else if (path_params != null && path_params.containsKey(name)) {
          // Si no es multipart, verificar query params
          System.out.println("  Found in path_params");
          found = true;
          paramValue = parseValue(path_params.get(name), parameter.getType());
        }

        if (!found) {
          System.out.println("  Parameter not found, required=" + required);
          if (required) {
            throw new HttpException(Status.BAD_REQUEST,
                "Parameter " + name + ":" + parameter.getType().getName() + " is required.");
          }
          paramValue = parseValue(null, parameter.getType());
        }

        System.out.println("  Final value: " + paramValue);
        values.add(paramValue);
        continue;
      }
      // RequestHeader
      annotation = parameter.getAnnotation(RequestHeader.class);
      if (annotation instanceof RequestHeader) {
        RequestHeader anot = (RequestHeader) annotation;
        try {
          values.add(parseValue(t.getRequestHeaders().get(anot.value()).get(0), parameter.getType()));
        } catch (Exception e) {
          if (anot.required()) {
            throw new HttpException(Status.BAD_REQUEST,
                "Request Header param " + anot.value() + " is required.");
          } else {
            values.add("");
          }

        }
        continue;
      }
      values.add(null);
    }

    // try {
    Object resp;
    resp = invoke(instance, values.toArray());
    response.setCode(Status.OK);
    // if (resp instanceof Servisofts.mediator.Response) {
    // Servisofts.mediator.Response r = (Servisofts.mediator.Response) resp;
    // r.status = response.getCode();
    // response.setBody(r.toString());
    // } else {
    response.setBody(resp.toString());
    // }
  }

  public Object invoke(Object instance, Object... arg)
      throws HttpException {
    if (this.method == null) {
      return null;
    }
    if (!this.method.trySetAccessible()) {
      return null;
    }
    try {
      return this.method.invoke(instance, arg);
    } catch (Exception e) {
      if (e.getCause() instanceof HttpException) {
        HttpException ex = (HttpException) e.getCause();
        throw new HttpException(ex.getCode(), ex.getMessage());
      } else {
        throw new HttpException(Status.BAD_REQUEST, "Error desconocido, " + e.getLocalizedMessage());
      }

    }
  }

  public Map<String, String> queryToMap(String query) {
    if (query == null) {
      return null;
    }
    Map<String, String> result = new HashMap<>();
    for (String param : query.split("&")) {
      String[] entry = param.split("=");
      if (entry.length > 1) {
        result.put(entry[0], entry[1]);
      } else {
        result.put(entry[0], "");
      }
    }
    return result;
  }

  private Map<String, Object> parseMultipartFormData(HttpExchange t, byte[] bodyBytes) throws HttpException {
    Map<String, Object> result = new HashMap<>();
    try {
      System.out.println("Body bytes read: " + (bodyBytes != null ? bodyBytes.length : 0));
      
      DiskFileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload upload = new ServletFileUpload(factory);
      
      List<FileItem> items = upload.parseRequest(new RequestContext() {
        @Override
        public String getCharacterEncoding() {
          return "UTF-8";
        }

        @Override
        public int getContentLength() {
          return bodyBytes.length;
        }

        @Override
        public String getContentType() {
          return t.getRequestHeaders().getFirst("Content-Type");
        }

        @Override
        public InputStream getInputStream() throws IOException {
          return new java.io.ByteArrayInputStream(bodyBytes);
        }
      });

      System.out.println("Multipart items parsed: " + items.size());
      
      for (FileItem item : items) {
        String fieldName = item.getFieldName();
        System.out.println("Field: " + fieldName + ", isFormField: " + item.isFormField());
        
        if (item.isFormField()) {
          // Es un campo de texto normal
          String value = item.getString("UTF-8");
          System.out.println("  -> Text value: " + value);
          result.put(fieldName, value);
        } else {
          // Es un archivo
          System.out.println("  -> File: " + item.getName() + ", size: " + item.getSize());
          byte[] fileContent = readInputStream(item.getInputStream());
          MultipartFile file = new MultipartFile(
            fieldName,
            item.getName(),
            item.getContentType(),
            fileContent
          );
          
          // Si ya existe el campo, convertir a lista
          if (result.containsKey(fieldName)) {
            Object existing = result.get(fieldName);
            if (existing instanceof List) {
              ((List<MultipartFile>) existing).add(file);
            } else {
              List<MultipartFile> list = new ArrayList<>();
              list.add((MultipartFile) existing);
              list.add(file);
              result.put(fieldName, list);
            }
          } else {
            result.put(fieldName, file);
          }
        }
      }
      
      System.out.println("Final multipart_params map keys: " + result.keySet());
      
    } catch (Exception e) {
      e.printStackTrace();
      throw new HttpException(Status.BAD_REQUEST, "Error parsing multipart/form-data: " + e.getMessage());
    }
    return result;
  }

  private byte[] readInputStream(InputStream inputStream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int read;
    byte[] data = new byte[8192];
    while ((read = inputStream.read(data, 0, data.length)) != -1) {
      buffer.write(data, 0, read);
    }
    return buffer.toByteArray();
  }

  public Object parseValue(Object value, Class<?> type) {

    if (type == String.class) {
      if (value == null)
        return "";
      return value.toString();
    }
    if (type == int.class) {
      if (value == null)
        value = 0;
      return Integer.parseInt(value.toString());
    }
    if (type == long.class) {
      if (value == null)
        value = 0;
      return Long.parseLong(value.toString());
    }
    if (type == double.class) {
      if (value == null)
        value = 0;
      return Double.parseDouble(value.toString());
    }
    if (type == boolean.class) {
      if (value == null)
        value = false;
      return Boolean.parseBoolean(value.toString());
    }
    if (type == Date.class) {
      if (value == null)
        return null;
      return new Date(Long.parseLong(value.toString()));
    }
    if (type == BigDecimal.class) {
      if (value == null)
        return null;
      return new BigDecimal(value.toString());
    }
    if (type == BigInteger.class) {
      if (value == null)
        return null;
      return new BigInteger(value.toString());
    }
    if (type == byte[].class) {
      if (value == null)
        return null;
      return value.toString().getBytes();
    }
    if (type == Byte.class) {
      if (value == null)
        return null;
      return Byte.parseByte(value.toString());
    }
    // Class[] i = type.getInterfaces();
    // if (i.length > 0) {
    // if (i[0].getName().equals(Request.class.getName())) {
    // return createRequest(type, value);
    // }
    // }
    // return JSON.getInstance().fromJson(value.toString(), type);
    if (value == null)
      return null;
    return value.toString();
  }

  public Object createRequest(Class type, Object value) {
    Object instance;
    try {
      Constructor[] constructors = type.getConstructors();
      for (Constructor constructor : constructors) {
        ArrayList<Object> values = new ArrayList<>();
        Class[] paramTypes = constructor.getParameterTypes();
        for (Class paramt : paramTypes) {
          // values.add(JSON.getInstance().fromJson(value.toString(), paramt));
        }
        instance = constructor.newInstance(values.toArray());
        return instance;
      }
      instance = type.getConstructor().newInstance();
    } catch (
        InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String getMethodSwagger() {
    return type.name().toLowerCase();
  }

  public Method getMethod() {
    return method;
  }

  public String getRoute() {
    return route;
  }

  public void setRoute(String route) {
    this.route = route;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public ActionType getType() {
    return type;
  }

  public void setType(ActionType type) {
    this.type = type;
  }

  // public Path getPathSwagger(Controller controller, String tag) {
  // String path = controller.getRoute() + getRoute();
  // Path po = new Path(path, getMethodSwagger());
  // String name = getMethod().getName();

  // Parameter[] parameters = this.method.getParameters();
  // int cant_params = 0;
  // for (Parameter parameter : parameters) {
  // Annotation annotation = parameter.getAnnotation(PathVariable.class);
  // if (annotation instanceof PathVariable) {
  // Servisofts.swagger.parts.Parameter pars = new
  // Servisofts.swagger.parts.Parameter(
  // this.params.get(cant_params),
  // "path",
  // true
  // );
  // cant_params++;
  // po.addParameter(pars);
  // }
  // annotation = parameter.getAnnotation(RequestBody.class);
  // if (annotation instanceof RequestBody) {
  // po.setRequestBody(new Servisofts.swagger.parts.RequestBody());
  // }
  // }
  // po.setOperationId(tag + "_" + name);
  // po.setSummary(tag + " " + name);
  // po.addTag(tag);
  // return po;
  // }
}
