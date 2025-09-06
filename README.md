# Apps Manager

![Java](https://img.shields.io/badge/Java-1.8+-orange.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

A powerful Java-based framework for dynamically loading, executing, and managing isolated JavaScript tasks. Built for extensibility and control, it allows you to run scripts that can interact with the system, communicate with each other, and perform complex operations like HTTP requests and file I/O, all within a secure, managed environment.

Perfect for building plugin systems, automation tools, or any application requiring dynamic, user-defined logic execution.

## ✨ Key Features

*   **Dynamic Task Loading:** Load JavaScript tasks from local JAR resources or remote URLs (e.g., AWS S3).
*   **Task Isolation & Lifecycle Management:** Each task runs in its own context. The system can start, stop, and restart tasks (e.g., based on version updates).
*   **Inter-Task Communication:** Tasks can send messages to each other using a built-in event bus system.
*   **Rich JavaScript API:** Tasks have access to a comprehensive set of utilities for:
    *   **File System Operations:** Read, write, and delete files.
    *   **HTTP Client:** Make `GET` and `POST` requests.
    *   **Image Processing:** Load and manipulate images via `BufferedImage`.
    *   **Data Encoding:** Base64 encode/decode (`atob`, `btoa`).
    *   **Logging:** Log messages with different severity levels (`info`, `warn`, `error`, etc.).
    *   **System Interaction:** Sleep threads, access system properties, and get the current working directory.
*   **Scheduled Tasks:** Easily create tasks that run on a fixed schedule using the `AbstractScheduledTask` base class.
*   **Dependency Injection:** Uses Google Guice for clean and testable code.
*   **Configuration:** Supports external configuration via a `config.json` file.

## 🚀 Getting Started

### Prerequisites

*   Java 8 or higher
*   Maven (for building from source)

### Building from Source

1.  Clone the repository.
2.  Navigate to the project directory.
3.  Build the JAR with dependencies:
    ```bash
    mvn clean package
    ```
    This will generate an executable JAR file named `apps-manager.jar` in the `target/` directory.

### Running the Application

The application is started by providing `customer` and `accessKey` flags. These are used to fetch the customer-specific task configuration and scripts.

```bash
java -jar apps-manager.jar --customer=your_customer_name --accessKey=your_access_key
```

**Optional Flag:**
*   `--use-local-tasks`: If provided, the application will load JavaScript tasks from within the JAR file instead of from the cloud (S3).

## 🧩 Core Architecture

*   **`Launcher`:** The main entry point. Parses command-line arguments and bootstraps the application.
*   **`TasksManager`:** The heart of the system. Responsible for loading, starting, stopping, and managing the lifecycle of all tasks. It also handles inter-task messaging.
*   **`AbstractScriptContext`:** The base class that defines the contract between the Java runtime and the JavaScript tasks. It exposes all Java methods and utilities to the JS environment.
*   **`Task` & `TaskContext`:** Represent a JavaScript task and its runtime context (including the script engine and execution future).
*   **`Module`:** Configures dependency injection (Guice) and sets up core services like thread pools and the HTTP client.
*   **`common.js`:** A JavaScript file bundled with the application that provides helper functions (like `spawnTask`, `sendMessage`, `httpGet`, etc.) to make interacting with the Java API more natural for JS developers.

## 📝 Example JavaScript Task

Your JavaScript tasks can leverage the provided API to perform complex operations. Here's a simple example:

```javascript
// Define a function to handle incoming messages from other tasks
defineTaskMessageHook(function(message) {
    console.log("Received message: " + JSON.stringify(message));
});

// Define a cleanup function when the task is stopped
defineTaskStopHook(function() {
    console.log("Cleaning up before shutdown...");
    destroyTaskDirectory(); // Clean up any files this task created
});

// Main task logic
console.log("Hello from " + getTaskName() + "!");

// Load a remote configuration file
var config = loadFile("https://example.com/config.json", true);
console.log("Loaded config: " + config);

// Send a message to another task
sendMessage({ action: "status_update", status: "running" }, "AnotherTaskName");

// Schedule a recurring job
var MyScheduledTask = makeScheduledTaskClass({
    runOneIteration: function() {
        console.log("This runs every 10 seconds!");
        // Perform scheduled work here
    }
});

var scheduledTask = new MyScheduledTask(10); // Run every 10 seconds
scheduledTask.startAsync();
```

## 📂 Project Structure

*   `src/main/java/io/github/apoterenko/apps/manager/`: Core Java classes.
*   `src/main/java/io/github/apoterenko/apps/manager/protocol/`: Classes for internal command and message structures.
*   `src/main/resources/`: Contains the `common.js` file and potentially other local task scripts.

## 🤝 Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.