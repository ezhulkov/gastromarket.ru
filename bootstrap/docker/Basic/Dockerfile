FROM ubuntu:latest
MAINTAINER Eugene Zhulkov ezhulkov@gmail.com

RUN apt-get update && apt-get install -y \
    zsh \
    mc \
    sudo \
    supervisor

RUN useradd -b /home -c "Eugene Zhulkov" -m -s /bin/zsh ezhulkov
RUN locale-gen en_US.UTF-8 ru_RU.UTF-8

ENV LANG en_US.UTF-8

CMD ["/bin/zsh"]