# Password Manager
## Contents
- [Overview](#overview)
- [Getting Started](#getting-started)
- [Usage](#usage)
  - [Login Screen](#login-screen)
  - [User Screen](#user-screen)
- [Architecture Documentation](#architecture-documentation)
- [Directory Structure](#directory-structure)

## Overview
Password manager provides a simple and portable application that stores your passwords. Multiple different master accounts can be made, meaning you can use the password manager on shared devices securely. You will be prompted to enter a unique login with each launch in order to decrypt and access your saved passwords.

Please be sure to read the license for information on fair use and applicable copyright laws in your country and/or jurisdiction.



## Getting Started
To get started, please navigate to this repository's releases page or click the link below to download the latest .zip package. Be sure to check back periodically for feature and security updates.

[Releases](https://github.com/DuaLee/password-manager/releases)

## Usage
### Login Screen
The package can be placed anywhere on your computer after extraction, however, it is recommended that you place it in its own folder, as the program directly interacts with your file system in order to create user accounts and temporary files.

[![image.png](https://i.postimg.cc/7LLsFxGY/image.png)](https://postimg.cc/CRWsDpsW)

Upon first run of the program, please be sure to create a new user by typing in a memorable but secure master username and password, then clicking "New User".

[![image.png](https://i.postimg.cc/ZqVkN730/image.png)](https://postimg.cc/Z9BDtLwh)

Afterwards, you will be able to see your username appear in your folder. At this point, you can click login with the already filled in information.

[![image.png](https://i.postimg.cc/jj9pvWgr/image.png)](https://postimg.cc/hJLZtGRC)

### User Screen
In the user screen, you can choose between "Add/Modify", and "Delete". The former will create a new entry for your user, while the latter will attempt to search and remove the entry with the entry name you have specified.

[![image.png](https://i.postimg.cc/85z7d00Y/image.png)](https://postimg.cc/zL4D805w)

You can also change your master account information in the "Update Account" pane. You can choose to update one or both of your username and password.

[![image.png](https://i.postimg.cc/7L6HP9kG/image.png)](https://postimg.cc/PCgs2mNT)

Once you have completed your operations, be sure to click the "x" on the top bar of the window to logout and reencrypt your files.

[![image.png](https://i.postimg.cc/5y31P4Qq/image.png)](https://postimg.cc/BPXdbGgX)

## Architecture Documentation
### Process Flowchart
![image.png](https://github.com/DuaLee/password-manager/blob/main/doc/Password%20Manager.png)

### Package: encryptor
#### MasterEncryptor
Utilizes the Java Crypto library to encrypt and decrypt a user's password manager file using a symmetric DES encryption scheme. A user account is encrypted using the user defined password, and is later encrypted using the same password upon logout. All the hashes and temporary files that are created by the encryptor are kept in ciphertext in the package's main directory to protect against eavesdropping attacks.

### Package: UI
#### LoginScreen
Prompts the user to input a username and password. If a user is found, a new file is created with a user defined username, then the file and password are passed onto the MasterEncryptor to be encrypted on the spot. The user is then prompted to reenter their password, verifying the intended password with the user. If the user succeeds to login, the window view and the user information is passed on the the UserScreen.

#### UserScreen
The user will be shown a Java Swing JTable that is populated, upon initialization and after every update, by the decrypted userdata. The table's rows contain all of the entryname/username/password combos that the user has created. When an action is triggered by the user, the UserScreen will modify the userdata file with the provided parameters. Upon logout, the password (key), and the userdata is passed to the MasterEncryptor to be re-encrypted. Once the file is secure, the application is exited.

## Directory Structure
| File | Description |
| --- | --- |
| doc | Resources used by the readme documentation |
| img | Image resources used by the program |
| out | Program output artifacts |
| src | Source code |
