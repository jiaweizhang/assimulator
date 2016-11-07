#!/bin/bash

#install git
sudo apt-get install git

#allow add-apt-repository
sudo apt-get install software-properties-common

#curl
sudo apt-get install curl

#install java
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
sudo apt-get install oracle-java8-set-default

#install sdkman
sudo apt-get install unzip
curl -s "https://get.sdkman.io" | bash

#install gradle
sdk install gradle 2.14.1

#install npm
sudo apt-get install npm

#npm no sudo
git clone https://github.com/glenpike/npm-g_nosudo.git
cd npm-g_nosudo/
./npm-g-nosudo.sh
source ~/.bashrc

#fix nodejs -> node
sudo ln -s /usr/bin/nodejs /usr/bin/node

#repo
git clone https://github.com/jiaweizhang/assembler-disassembler-simulator.git
cd assembler-disassembler-simulator/
cd src/main/webapp/
npm install
