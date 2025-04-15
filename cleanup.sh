#!/bin/bash

# Change this if your Java program has a specific name
JAVA_KEYWORD="Main"  # or any unique word from the command like "your-jar-name" or "robot"
SERIAL_PORT="/dev/ttyACM0"
SOCKET_PORT=1234

echo "🧹 Cleaning up robot environment..."

# 1. Kill any Java processes that might still be running the robot program
echo "🔪 Killing Java processes related to robot..."
PIDS=$(ps aux | grep java | grep "$JAVA_KEYWORD" | awk '{print $2}')
if [ -z "$PIDS" ]; then
    echo "✅ No leftover Java processes found."
else
    echo "$PIDS" | xargs kill -9
    echo "✅ Killed leftover Java processes: $PIDS"
fi

# 2. Free the serial port if anything is holding it
echo "🔌 Releasing serial port $SERIAL_PORT..."
sudo fuser -k "$SERIAL_PORT" 2>/dev/null
if [ $? -eq 0 ]; then
    echo "✅ Serial port released."
else
    echo "ℹ️ Serial port not in use or already free."
fi

# 3. Clear socket in TIME_WAIT state (optional: requires root + netstat)
echo "🌐 Checking for open socket on port $SOCKET_PORT..."
SOCKET_PIDS=$(sudo lsof -i :$SOCKET_PORT -t 2>/dev/null)
if [ ! -z "$SOCKET_PIDS" ]; then
    echo "🔪 Killing processes using port $SOCKET_PORT: $SOCKET_PIDS"
    echo "$SOCKET_PIDS" | xargs sudo kill -9
else
    echo "✅ Socket port $SOCKET_PORT is free."
fi

echo "🎉 Clean-up complete!"
