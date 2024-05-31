package edu.tufts.hrilab.gui;

import ai.thinkingrobots.trade.TRADE;
import ai.thinkingrobots.trade.TRADEServiceConstraints;
import ai.thinkingrobots.trade.TRADEServiceInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import edu.tufts.hrilab.fol.Factory;
import edu.tufts.hrilab.fol.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Tuple3d;

@SpringBootApplication
@RestController
public class DemoApplication extends SpringBootServletInitializer {

  private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

  @Autowired
  private TradeServiceIntegrator tradeServiceIntegrator;

  @Autowired
  private TradeServiceTracker tradeServiceTracker;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(DemoApplication.class);
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @GetMapping("/services")
  public Map<String, Set<Map<String, String>>> getServices() {
    return tradeServiceIntegrator.getServicesOrganized();
  }

  @GetMapping("/hello")
  public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
  }

  @GetMapping("/goodbye")
  public String goodbye(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Goodbye %s!", name);
  }

  // an example only working for parameter is string
  // http://localhost:8080/invoke-example?serviceName=exampleMethod&parameter=Hello%20World
  @GetMapping("/invoke-example")
  public ResponseEntity<String> invokeExample(@RequestParam("serviceName") String serviceName, @RequestParam("parameter") String parameter) {
    try {
      // Call the specified service with the provided parameter
      TRADE.getAvailableService(new TRADEServiceConstraints().name(serviceName).argTypes(String.class)).call(Object.class, parameter);

      return ResponseEntity.ok("Service '" + serviceName + "' invoked successfully with parameter: " + parameter);
    } catch (Exception e) {
      e.printStackTrace();  // Log the exception for debugging
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to invoke service '" + serviceName + "': " + e.getMessage());
    }
  }

  @GetMapping("/list-trade-services")
  public Set<String> listTradeServices() {
    Collection<TRADEServiceInfo> services = TRADE.getAvailableServices();
    return services.stream()
            .map(service -> service.serviceString)
            .collect(Collectors.toSet());
  }

  @CrossOrigin(origins = "http://localhost:3000")
  @PostMapping("/invoke-service")
  public ResponseEntity<String> invokeService(
          @RequestParam("serviceName") String serviceName,
          @RequestBody(required = false) JsonNode rawArgs) { // Use JsonNode to raw handle incoming JSON

      String baseServiceName = null;
      try {
          // Obtain the argument types for the specified service
          Map<String, String[]> servicesToTrack = TradeServiceTracker.parseServiceStrings(listTradeServices());
          baseServiceName = serviceName.split("_")[0];
          String[] argTypes = servicesToTrack.get(baseServiceName);

          if (argTypes == null) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service name '" + baseServiceName + "' is not recognized or is missing argument types.");
          }

          Object[] args; // Prepare the arguments array
          Class<?>[] argClasses;

          if (Objects.requireNonNull(rawArgs.getNodeType()) == JsonNodeType.OBJECT) {// Check if service documentation is loaded for this service
              Map<String, Map<String, Object>> serviceDetails = DocumentationController.loadServiceDocumentation();
              if (!serviceDetails.containsKey(serviceName)) {
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service documentation missing for: " + serviceName);
              }

              Map<String, Object> paramsInfo = serviceDetails.get(serviceName);
              List<Map<String, Object>> parameters = (List<Map<String, Object>>) paramsInfo.get("parameters");

              args = new Object[parameters.size()];
              argClasses = new Class<?>[parameters.size()];

              int index = 0;
              for (Map<String, Object> paramInfo : parameters) {
                  String paramName = (String) paramInfo.get("name");
                  String typeName = (String) paramInfo.get("type");

                  if (!rawArgs.has(paramName)) {
                      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing parameter: " + paramName);
                  }

                  JsonNode paramValue = rawArgs.get(paramName);
                  String valueAsString = paramValue.isValueNode() ? paramValue.asText() : paramValue.toString();
                  args[index] = convertToType(valueAsString, typeName);
                  assert args[index] != null;
                  argClasses[index] = args[index].getClass();
                  index++;
              }
//              depreciating
//              case ARRAY:
//                  // Check if the service expects a single List argument
//                  // && rawArgs != null
//                  if (argTypes.length == 1 && argTypes[0].equals("java.util.List") && rawArgs.isArray()) {
//                      // Treat the entire array as a single List argument
//                      String listValue = rawArgs.toString(); // Convert the raw JSON array to String
//                      Object listArg = convertToType(listValue, "java.util.List");
//                      args = new Object[]{listArg};
//                      argClasses = new Class<?>[]{List.class};
//                  } else {
//                      // Treat each item in the JSON array as separate arguments
//                      args = new Object[argTypes.length];
//                      argClasses = new Class<?>[argTypes.length];
//                      for (int i = 0; i < argTypes.length; i++) {
//                          if (!rawArgs.has(i)) {
//                              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing argument at index " + i + " for service '" + baseServiceName + "'.");
//                          }
//                          String typeName = argTypes[i];
//                          String value;
//                          if ("javax.vecmath.Matrix4d".equals(typeName) && rawArgs.has(i) && rawArgs.get(i).isArray()) {
//                              value = rawArgs.get(i).toString(); // Directly convert the JSON array to string
//                          } else {
//                              value = rawArgs.has(i) ? rawArgs.get(i).asText() : null; // Use JsonNode API to get value
//                          }
//                          args[i] = convertToType(value, typeName);
//                          assert args[i] != null;
//                          argClasses[i] = args[i].getClass();
//                      }
//                  }
//                  break;
          } else {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON input format.");
          }

          TRADEServiceConstraints constraints = new TRADEServiceConstraints().name(baseServiceName).argTypes(argClasses);
          Object result = TRADE.getAvailableService(constraints).call(Object.class, args);

          // Convert the result to a JSON string to include in the response
          String jsonResult = new ObjectMapper().writeValueAsString(result);
          return ResponseEntity.ok("Service '" + baseServiceName + "' invoked successfully with arguments: " + Arrays.toString(args) + " and returned: " + jsonResult);
      } catch (Exception e) {
          e.printStackTrace();
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to invoke service '" + baseServiceName + "': " + e.getMessage());
      }
  }

  /**
   * Converts a string value to an object of the specified type.
   * Might need to be extended based on the specific types used in your application.
   *
   * @param value    The string value to convert.
   * @param typeName The type name to convert the value to.
   * @return The object of the specified type.
   * @throws Exception If the conversion fails or the type is unsupported.
   */
  private Object convertToType(String value, String typeName) throws IOException, ClassNotFoundException {
    ObjectMapper mapper = new ObjectMapper();
    System.out.println("Converting value: " + value + " to type: " + typeName); // Log the value and type being converted

    switch (typeName) {
//      case "java.lang.String":
//        return
      case "java.lang.String":
      case "string":
        return value;
      case "java.lang.Integer":
      case "int":
        return Integer.parseInt(value);
      case "java.lang.Double":
      case "double":
        return Double.parseDouble(value);
      case "java.lang.Boolean":
      case "boolean":
        return Boolean.parseBoolean(value);
      case "java.util.List":
      case "list":
        JsonNode rootNode = mapper.readTree(value);
        if (rootNode.isArray()) {
          List<Object> list = new ArrayList<>();
          System.out.println("List detected in JSON: " + rootNode.toString()); // Print the entire list for debugging
          for (JsonNode node : rootNode) {
            if (node.isTextual()) {
              String text = node.asText();
              System.out.println("Processing list item: " + text); // Print each list item
              // Assuming a "name:type" representation for a Variable or Symbol
              try {
                Variable var = Factory.createVariable(text);
                list.add(var);
                System.out.println("Successfully created Variable: " + text);
              } catch (Exception e) {
                System.out.println("Failed to create Variable from: " + text);
                // Handle failure to create Variable
              }
            } else {
              // Handle other types of list elements as needed
              System.out.println("Non-textual list item encountered: " + node.toString());
            }
          }
          return list;
        } else {
          System.out.println("Expected a JSON array but found: " + rootNode.getNodeType()); // Log unexpected node type
        }
        break;
      case "edu.tufts.hrilab.fol.Symbol":
      case "symbol":
        // Using "name:type" Syntax
        return Factory.createSymbol(value);
      case "edu.tufts.hrilab.fol.Variable":
      case "variable":
        return Factory.createVariable(value);
      case "javax.vecmath.Matrix4d":
      case "matrix4d":
        try {
          JsonNode arrayNode = mapper.readTree(value);
          if (!arrayNode.isArray() || arrayNode.size() != 16) {
            throw new IllegalArgumentException("Matrix4d requires an array of 16 values.");
          }
          double[] matrixValues = new double[16];
          for (int i = 0; i < 16; i++) {
            matrixValues[i] = arrayNode.get(i).asDouble();
          }
          Matrix4d matrix = new Matrix4d(matrixValues);
          System.out.println("Successfully created Matrix4d from: " + Arrays.toString(matrixValues));
          return matrix;
        } catch (Exception e) {
          System.out.println("Error parsing Matrix4d values from: " + value);
          throw new IllegalArgumentException("Invalid format for Matrix4d values.", e);
        }
      default:
        System.out.println("Unsupported argument type encountered: " + typeName); // Log unsupported type
        throw new IllegalArgumentException("Unsupported argument type: " + typeName);
    }
    return null;
  }
}