var $$scope = this;

// Version 1.0
var makeClass = function (cls) {
  return Java.type(cls);
};

// Version 1.0
var systemClass = makeClass('java.lang.System');
var objectClass = makeClass('java.lang.Object');
var Objects = makeClass('java.util.Objects');
var IOUtils = makeClass('org.apache.commons.io.IOUtils');

// Version 1.0
var superClass = function (superCls) {
  return Java.super(superCls);
};

// Version 1.0
var extendClass = function (parentCls, config) {
  return Java.extend(typeof parentCls === 'string' ? makeClass(parentCls) : parentCls, config);
};

// Version 1.0
var makeAppClass = function (cls) {
  return makeClass([$$packageContextName, cls].join('.'))
};

// Version 1.0
var makeAppProtocolClass = function (cls) {
  return makeAppClass('protocol.' + cls)
};

// Version 1.0
var makeAppCommandClass = function () {
  return makeAppProtocolClass('Command')
};

// Version 1.0
var makeAppCommandResultClass = function () {
  return makeAppProtocolClass('CommandResult')
};

// Version 1.0
var makeAppBaseCommandClass = function () {
  return makeAppProtocolClass('BaseCommand')
};

// Version 1.0
var makeAppObjectStatusClass = function () {
  return makeAppProtocolClass('ObjectStatus')
};

// Version 1.0
var makeAppPayloadWrapperClass = function () {
  return makeAppProtocolClass('PayloadWrapper')
};

// Version 1.0
var AbstractScheduledTaskClass = makeAppClass('AbstractScheduledTask');

// Version 1.0
var makeScheduledTaskClass = function (config) {
  return extendClass(AbstractScheduledTaskClass, config);
};

// Version 1.0
var getSystemProperty = function (key) {
  return systemClass.getProperty(key);
};

// Version 1.0
var getProxyHostProperty = function () {
  return $$taskContext.getProxyHostProperty();
};

// Version 1.0
var isProductionModeEnabled = function () {
  return $$taskContext.isProductionModeEnabled();
};

// Version 1.0
var toJson= function (o) {
  return $$taskContext.toJson(o);
};

// Version 1.0
var console = {
  // Version 1.0
  log: function (s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10) {
    $$taskContext.logInfo(s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);
  },
  // Version 1.0
  warn: function (s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10) {
    $$taskContext.logWarn(s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);
  },
  // Version 1.0
  error: function (s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10) {
    $$taskContext.logError(s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);
  },
  // Version 1.0
  debug: function (s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10) {
    $$taskContext.logDebug(s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);
  },
  // Version 1.0
  trace: function (s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10) {
    $$taskContext.logTrace(s, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);
  }
};

// Version 1.0
var spawnTask = function (callback, args) {
  return $$taskContext.spawnTask(callback, args);
};

// Version 1.0
var getAtomicReference = function () {
  return $$taskContext.getAtomicReference();
};

/**
 * @stable [01.05.2018]
 * @version 1.0
 * @returns {*}
 */
var getExecutingTasks = function () {
  return $$taskContext.getExecutingTasks();
};

/**
 * @stable [01.05.2018]
 * @version 1.0
 * @returns {*}
 */
var getExecutingTaskContext = function (taskName) {
  return $$taskContext.getExecutingTaskContext(taskName);
};

/**
 * @stable [01.05.2018]
 * @version 1.0
 * @returns {*}
 */
var getWorkingDirectory = function () {
  return $$taskContext.getWorkingDirectory();
};

/**
 * @stable [01.05.2018]
 * @version 1.0
 * @returns {*}
 */
var getWorkingTaskDirectory = function () {
  return $$taskContext.getWorkingTaskDirectory();
};

// Version 1.0
var defineTaskStopHook = function (hookFn) {
  $$scope.$$onTaskStopHook = hookFn;
};

// Version 1.0
var defineTaskMessageHook = function (hookFn) {
  $$scope.$$onTaskMessageHook = hookFn;
};

// Version 1.0
var sendMessage = function (message, taskName) {
  isDef(taskName)
    ? $$taskContext.sendMessage(message, taskName)
    : $$taskContext.sendMessage(message);
};

// Version 1.0
var isDef = function (o) {
  return typeof o !== 'undefined';
};

// Version 1.0
var isString = function (o) {
  return typeof o === 'string';
};

// Version 1.0
var isObject = function (o) {
  return typeof o === 'object';
};

// Version 1.0
var isNull = function (o) {
  return o === null;
};

/**
 * @stable [01.05.2018]
 * @version 1.0
 * @returns {*}
 */
var join = function (separator, parts) {
  return $$taskContext.join(separator, parts);
};

// Version 1.0
var sleep = function (v) {
  try {
    $$taskContext.sleep(typeof v === 'number' ? v : -1);
  } catch (ignored) {
  }
};

// Version 1.0
var readBufferedImage = function (data) {
  return $$taskContext.readBufferedImage(data);
};

// Version 1.0
var readFile = function (fileName) {
  return $$taskContext.readFile(fileName);
};

// Version 1.0
var writeFile = function (data, fileName) {
  return $$taskContext.writeFile(data, fileName);
};

// Version 1.0
var removeFile = function (data, fileName) {
  return $$taskContext.removeFile(data, fileName);
};

// Version 1.0
var loadFile = function (path, asString) {
  return $$taskContext.loadFile(path, asString);
};

/**
 * @stable [01.05.2018]
 * @version 1.0
 * @returns {*}
 */
var readIO = function (io, asString) {
  return $$taskContext.readIO(io, asString);
};

/**
 * @stable [01.05.2018]
 * @version 1.0
 * @returns {*}
 */
var readIOAsString = function (io) {
  return $$taskContext.readIOAsString(io);
};

// Version 1.0
var destroyTaskDirectory = function () {
  return $$taskContext.destroyTaskDirectory();
};

// Version 1.0
var httpGet = function (path) {
  return $$taskContext.httpGet(path);
};

/**
 * @stable [14.08.2018]
 * @version 1.0
 * @returns {*}
 */
var httpPost = function (path, type, data) {
  return $$taskContext.httpPost(path, type, data);
};

/**
 * @stable [01.05.2018]
 * @version 1.0
 * @returns {*}
 */
var getTaskName = function () {
  return $$taskContext.getTaskName();
};

/**
 * @stable [01.05.2018]
 * @version 1.0
 * @returns {*}
 */
var getCurrentTask = function () {
  return $$taskContext.getCurrentTask();
};

// Version 1.0
var atob = function (data) {
  return $$taskContext.atob(data);
};

// Version 1.0
var btoa = function (data) {
  return $$taskContext.btoa(data);
};

// Version 1.0
var hash = function (data) {
  return Objects.hash(data);
};

/**
 * @stable [14.06.2018]
 * @version 1.0
 * @returns {*}
 */
var makeSocketAddress = function (host, port) {
  return $$taskContext.makeSocketAddress(host, port);
};