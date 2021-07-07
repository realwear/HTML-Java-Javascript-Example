//
//How to call Java method and receive the spoken command from Java/Kotlin!
//

//This function provides an array of the commands we want to get use as our new set of available commands
function setUpVoiceCommands(){
    let commands = ['command one','command two', 'command three'];
    callJavaMethod(commands);
}

//This method will call a java interface method and will change the old commands to the new set provided
function callJavaMethod(commands){
    let displayCommands = "Current Commands: \n";
    for (let i = 0; i < commands.length; i++) {
        if(i == 0)
            displayCommands += commands[i];
        else
            displayCommands += ", " + commands[i];
    }

    WearHFNative.updateVoiceCommands(commands);
    document.getElementById("commandButton").style.display = "none";
    document.getElementById("currentCommandsDisplay").value = displayCommands;
}

//This method is used by the java/kotlin side to send the spoken command back to JavaScript
function onReceiveCommand(command){
    //Example of how to react to different commands
    if(command == "set up new commands"){
        document.getElementById("commandButton").click();
    }

    //Display returned spoken command from java broadcast
    alert(command);
}
