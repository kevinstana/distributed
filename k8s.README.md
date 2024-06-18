# Configuring k8s machine
In the machine you want to deploy the app, make sure to download microk8s:
```bash
sudo snap install microk8s --classic
```
Find your network interface by running:  
```bash
ip a
```
replace `cni0` from the following commands with your network interface:
```bash
sudo ufw allow in on eth0 && sudo ufw allow out on eth0
sudo ufw default allow routed
```
To access microk8s without sudo, run the following commands:
```bash
sudo usermod -a -G microk8s $USER
mkdir ~/.kube
sudo chown -f -R $USER ~/.kube
sudo su - $USER
```
Enable add-ons:
```bash
microk8s.enable dns storage ingress
```
Redirect the certificate to access the cluster to `.kube/config`  
```bash
microk8s.kubectl config view --raw > $HOME/.kube/config
```
# Configuring your PC
Download kubectl:
```bash
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
```
Add kubectl to path:
```bash
sudo mv kubectl /usr/local/bin/kubectl
```
Download k9s:
```bash
curl -sS https://webinstall.dev/k9s | bash
```
Make a secret directory:
```bash
mkdir -p ~/.kube/
```
Now copy the `.kube/config` file from the k8s machine to your PC:  
```bash
scp <host>:/path/to/.kube/config ~/.kube
```
Open the `~/.kube/config` file on your PC with an editor. Replace `127.0.0.1` in the `server` with the IP of the k8s machine.  
Below `cluster`, delete the entirety of `certificate-authority-data`. Replace it with `insecure-skip-tls-verify: true`.  
As a final step, make sure port `16443` in the k8s machine is open.  
