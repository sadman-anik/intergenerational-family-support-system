#!/bin/zsh

echo "Starting IGFSS Client..."

cd client

javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml *.java

java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml MainApp