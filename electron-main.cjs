const { app, BrowserWindow, shell, dialog } = require("electron");
const path = require("node:path");
const fs = require("node:fs");

let mainWindow;

function writeStartupLog(message) {
  try {
    const logPath = path.join(app.getPath("userData"), "startup.log");
    fs.mkdirSync(path.dirname(logPath), { recursive: true });
    fs.appendFileSync(logPath, `[${new Date().toISOString()}] ${message}\n`, "utf8");
  } catch (error) {
    // Logging must never prevent the browser from starting.
  }
}

function showStartupError(title, error) {
  const detail = error && (error.stack || error.message) ? (error.stack || error.message) : String(error);
  writeStartupLog(`${title}: ${detail}`);
  try {
    dialog.showErrorBox(title, `${detail}\n\nIf Windows C: drive is full, free at least 2–5 GB and try again. Startup log location: ${path.join(app.getPath("userData"), "startup.log")}`);
  } catch (_) {}
}

function createWindow() {
  try {
    mainWindow = new BrowserWindow({
      width: 1440,
      height: 900,
      minWidth: 900,
      minHeight: 600,
      title: "APEX Browser",
      backgroundColor: "#080a12",
      autoHideMenuBar: true,
      show: false,
      webPreferences: {
        contextIsolation: true,
        nodeIntegration: false,
        sandbox: true
      }
    });

    mainWindow.once("ready-to-show", () => {
      writeStartupLog("Window ready; showing APEX Browser.");
      mainWindow.show();
    });

    mainWindow.webContents.on("did-fail-load", (_event, errorCode, errorDescription, validatedURL) => {
      writeStartupLog(`Page load failed: code=${errorCode}; ${errorDescription}; url=${validatedURL}`);
    });

    mainWindow.webContents.on("render-process-gone", (_event, details) => {
      writeStartupLog(`Renderer exited: ${JSON.stringify(details)}`);
    });

    mainWindow.webContents.setWindowOpenHandler(({ url }) => {
      if (/^https?:\/\//i.test(url)) shell.openExternal(url);
      return { action: "deny" };
    });

    mainWindow.loadFile(path.join(__dirname, "index.html")).catch((error) => {
      showStartupError("APEX Browser could not load its home page", error);
    });
  } catch (error) {
    showStartupError("APEX Browser failed to create its window", error);
  }
}

process.on("uncaughtException", (error) => showStartupError("APEX Browser startup error", error));
process.on("unhandledRejection", (error) => showStartupError("APEX Browser startup error", error));

app.whenReady().then(() => {
  writeStartupLog("Electron app ready; creating window.");
  createWindow();
  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
}).catch((error) => showStartupError("APEX Browser could not start", error));

app.on("window-all-closed", () => {
  if (process.platform !== "darwin") app.quit();
});