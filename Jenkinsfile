pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh '''cp -a /var/lib/jenkins/buildMetadata/AstralSorcery/. .
rm -rf AS-Example.zs perkMapDraft.pdn README.html README.md AstralSorcery
/var/lib/jenkins/workspace/AstralSorcery/gradlew build
cp -a ./build/libs/. .
rm -rf build gradle .gradle
find . ! -name \'*.jar\' -delete'''
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts '*.jar'
      }
    }
  }
}