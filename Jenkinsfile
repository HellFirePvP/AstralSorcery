pipeline {
  agent any
  environment {
    WEBHOOKURL = credentials('JenkinsDiscordWebhook')
  }
  stages {
    stage('Prepare Build') {
      steps {
        sh '''cp -a /var/lib/jenkins/buildMetadata/AstralSorcery-1.15/. .
rm -rf AS-Example.zs perkMapDraft.pdn README.html README.md AstralSorcery'''
      }
    }
    stage('Build only') {
      when {
        not {
          branch 'master'
        }
      }
      steps {
        sh '''./gradlew build'''
      }
    }
    stage('Build and Publish') {
      when{
        branch 'master'
      }
      steps {
        sh '''./gradlew build publish'''
      }
    }
    stage('Prepare Archive') {
      steps {
        sh '''cp -a ./build/libs/. .
rm -rf build gradle .gradle
find . ! -name \'*.jar\' -delete'''
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts '*.jar'
      }
    }
    stage('Notify') {
      when{
        branch 'master'
      }
      steps {
        discordSendHellFire link: env.BUILD_URL, result: currentBuild.currentResult, webhookURL: "${WEBHOOKURL}"
      }
    }
  }
}