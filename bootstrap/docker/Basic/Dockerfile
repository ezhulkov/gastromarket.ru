FROM ubuntu:latest
MAINTAINER Eugene Zhulkov ezhulkov@gmail.com

RUN apt-get update && apt-get install -y \
    zsh \
    git \
    ssh \
    mc \
    sudo

RUN useradd -b /home -c "Eugene Zhulkov" -m -s /bin/zsh ezhulkov
RUN mkdir /home/ezhulkov/.ssh && chmod 700 /home/ezhulkov/.ssh
COPY authorized_keys /home/ezhulkov/.ssh/authorized_keys
RUN chown -R ezhulkov:ezhulkov /home/ezhulkov/.ssh
RUN chmod 700 /home/ezhulkov/.ssh
RUN chmod 600 /home/ezhulkov/.ssh/authorized_keys

EXPOSE 22
ENV LANG en_US.UTF-8

CMD ["/bin/zsh"]