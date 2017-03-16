#!/usr/bin/python

import os, sys, time, subprocess, shutil

startTime = time.time()

# Helper function to implement finding files with particular suffixes
#
# directory is a path to the root directory of the find
# suffix is required for the filename to have to be included in the find
# excludeSuffix is a list of suffixes to exclude from the search
# returns a list of files
def find(directory, suffix="", excludeSuffix=[]):
	found = []
	for path, dirs, files in os.walk(directory):
		for name in files:
			includeName = name.endswith(suffix)
			if not includeName:
				continue
			for exclude in excludeSuffix:
				if name.endswith(exclude):
					includeName = False
			if includeName:
				found.append(os.path.join(path, name))
	return found

def ensureDirectoryExists(path):
	if os.path.exists(path):
		if not os.path.isdir(path):
			os.remove(path)
			os.mkdir(path)
	else:
		os.makedirs(path)

def issueCommand(command):
	print " ".join(command)
	sys.stdout.flush()
	code = subprocess.call(command)
	if code != 0:
		raise RuntimeError("{} exited with nonzero exit code {}".format(command[0], code))

tasks = {}

def tasklist(thingToDo=None):
	if thingToDo == "description":
		return "Prints a list of tasks"
	if thingToDo == "dependencies":
		return []
	print "Available tasks:"
	for k,v in tasks.iteritems():
		print "{} - {}".format(k, v("description"))
tasks["tasks"] = tasklist

def clean(thingToDo=None):
	if thingToDo == "description":
		return "Cleans the output directory"
	if thingToDo == "dependencies":
		return []
	if not os.path.exists("out"):
		print "Nothing to clean"
		return
	shutil.rmtree("out")
tasks["clean"] = clean

def buildClasses(thingToDo=None):
	if thingToDo == "description":
		return "Builds the library classes"
	if thingToDo == "dependencies":
		return []
	ensureDirectoryExists("out/classes/main")
	sources = find("src", ".java")
	print "Found {} source files".format(len(sources))
	libraries = find("lib", ".jar", ["javadoc.jar", "sources.jar", "natives-windows.jar", "natives-linux.jar"])
	print "Found {} libraries".format(len(libraries))
	classpath = ""
	for lib in libraries:
		classpath += os.pathsep
		classpath += lib
	classpath = classpath[1:]
	command = ["javac", "-d", "out/classes/main", "-cp", classpath]
	for f in sources:
		command.append(f)
	issueCommand(command)
tasks["buildClasses"] = buildClasses

def buildTestClasses(thingToDo=None):
	if thingToDo == "description":
		return "Builds the test classes"
	if thingToDo == "dependencies":
		return ["buildClasses"]
	ensureDirectoryExists("out/classes/test")
	sources = find("test", ".java")
	print "Found {} test source files".format(len(sources))
	libraries = find("lib", ".jar", ["javadoc.jar", "sources.jar", "natives-windows.jar", "natives-linux.jar"])
	print "Found {} libraries".format(len(libraries))
	classpath = "out/classes/main"
	for lib in libraries:
		classpath += os.pathsep
		classpath += lib
	command = ["javac", "-d", "out/classes/test", "-cp", classpath]
	for f in sources:
		command.append(f)
	issueCommand(command)
tasks["buildTestClasses"] = buildTestClasses

def jar(thingToDo=None):
	if thingToDo == "description":
		return "Builds the library jars. These jars do no include dependencies."
	if thingToDo == "dependencies":
		return ["buildClasses"]
	if os.path.exists("out/binJar"):
		shutil.rmtree("out/binJar")
	print "Copying {} to {}".format(os.path.join("out","classes","main"), os.path.join("out","binJar"))
	shutil.copytree("out/classes/main", "out/binJar")
	files = find("src", excludeSuffix=[".java"])
	for i, path in enumerate(files):
		files[i] = path[4:] # Sorry for the magic number :(
	print "Found {} jar resource files".format(len(files))
	for f in files:
		print "Copying {} to {}".format(os.path.join("src",f), os.path.join("out","binJar",f))
		ensureDirectoryExists(os.path.join("out"+os.path.sep+"binJar",os.path.dirname(f)))
		shutil.copy2(os.path.join("src", f), os.path.join("out"+os.path.sep+"binJar",f))
	issueCommand(["jar", "cf", "out/mtk.jar", "-C", "out/binJar/", "."])
tasks["jar"] = jar

def runTest(thingToDo=None):
	if thingToDo == "description":
		return "Runs the test class"
	if thingToDo == "dependencies":
		return ["buildTestClasses", "jar"]
	libraries = find("lib", ".jar", ["javadoc.jar", "sources.jar"])
	libraries.append("out/mtk.jar")
	print "Found {} libraries".format(len(libraries))
	classpath = "out/classes/test"
	for lib in libraries:
		classpath += os.pathsep
		classpath += lib
	issueCommand(["java", "-cp", classpath, "minusk.mtk.test.Test"])
tasks["runTest"] = runTest

completedTasks = {}

def executeTask(task):
	if not task in tasks:
		print "Invalid task: '{}'".format(task)
	elif not task in completedTasks:
		deps = tasks[task]("dependencies")
		for dep in deps:
			executeTask(dep)
		print "Executing task '{}'".format(task)
		tasks[task]()
		completedTasks[task] = True

try:
	args = sys.argv[1:]
	if len(args) == 0:
		print "No tasks specified."
	else:
		for task in args:
			executeTask(task)
finally:
	print "Done. Took {} ms.".format(int((time.time()-startTime)*1000))
