node {
  // Mark the code checkout 'stage'....
  stage 'Checkout'
  // Get some code from a GitHub repository
 
  git url: '...'
  // Clean any locally modified files and ensure we are actually on origin/master
  // as a failed release could leave the local workspace ahead of origin/master
  bat "git clean -f && git reset --hard origin/master"
  def mvnHome = tool 'maven-3.3.9'
  // we want to pick up the version from the pom
  def pom = readMavenPom file: 'pom.xml'
  def version = pom.version.replace("-SNAPSHOT", ".${currentBuild.number}")
  // Mark the code build 'stage'....
  stage 'Build'
  // Run the maven build this is a release that keeps the development version 
  // unchanged and uses Jenkins to provide the version number uniqueness
  bat "${mvnHome}/bin/mvn -DreleaseVersion=${version} -DdevelopmentVersion=${pom.version} -DpushChanges=false -DlocalCheckout=true -DpreparationGoals=initialize release:prepare release:perform -B"
  // Now we have a step to decide if we should publish to production 
  // (we just use a simple publish step here)
  input 'Publish?'
  stage 'Publish
}