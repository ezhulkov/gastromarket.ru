#!/bin/sh

mvn clean install -DskipTests=true -P prod
ansible-playbook -i bootstrap/ansible/prod bootstrap/ansible/deploy.yml

