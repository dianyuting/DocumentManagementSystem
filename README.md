# 项目介绍
* 本项目是一个简易的个人文档管理系统。<br>
  主要由用户登录、注册和信息修改，文档的上传、下载、删除、添加标签、好友分享，标签的添加、删除、修改，分组的添加、删除、修改，好友的查找添加、删除、修改等部分组成。
* 此项目采用vue+SpringBoot+MyBatis-Plus，使用ftp服务器存放文件，JWT验证用户登录，Redis和smtp协议重置用户密码。
* 开发工具：idea+VSCode+Navicat+VMware<br>
开发环境：jdk17+mysql5+tomcat10
  ![image](https://github.com/user-attachments/assets/ee2ccb4e-7a64-4b21-aa5c-1c7c34642188)
# 功能模块介绍
<details>
<summary><h2>1、用户信息模块</h2></summary>
<details>
<summary><h3>1.1、用户登录</h3></summary>
用户在登录界面通过输入用户名和密码进行账号登录。如无账号，可点击注册按钮进行注册；
如忘记密码可点击忘记密码按钮，可选择邮箱验证和密保问题验证，未进行密保问题设置则不能进行密保验证。
登录后通过个人信息对账号基本信息进行修改，可通过账号安全对密保信息进行设置，可通过修改密码对原密码进行修改。<br>
  
![image](https://github.com/user-attachments/assets/88cf7ec2-beee-4afe-8e3a-eb8d925c83bf)

</details>
<details>
<summary><h3>1.2、用户注册</h3></summary>
注册用户时，后台会获取注册信息，判断用户名、邮箱、手机号是否重复，重复则注册失败，弹出错误提示。该系统会提供随机生成用户名按钮，使用户有良好的注册体验。<br>
  
![image](https://github.com/user-attachments/assets/0c11ce9d-a2e0-4a0e-a5cd-58d536ac1037)

</details>
<details>
<summary><h3>1.3、身份验证</h3></summary>
当用户忘记密码时，可输入用户名，点击忘记密码按钮使用身份验证功能重置密码。
可以进行邮箱验证和密保验证用于重置密码，邮箱验证可向用户邮箱发送一个4位数的验证码，密保验证需用户回答已设置的问题，用户验证成功则可重置密码。
</details>
<details>
<summary><h3>1.4、信息修改</h3></summary>
主界面中可进行个人信息、修改密码、账号安全、退出登录。
个人信息可修改用户大部分信息，会判断邮箱和手机号是否重复，重复则弹出相应提示。账号安全可以设置密保问题，后端会验证密码是否输入正确。修改密码需要先输入原密码进行验证。
</details>
</details>
<details>
<summary><h2>2、分组管理模块</h2></summary>
点击好友列表，会出现分组信息，可添加分组、编辑分组、删除分组。
添加分组和编辑分组时，会验证分组名是否重复，重复会返回错误提示。删除分组时，会同时删除改分组下的所有好友，前端设置弹出提示，提醒用户谨慎操作。
</details>
<details>
<summary><h2>3、好友管理模块</h2></summary>
好友列表按分组显示所有好友信息，鼠标悬浮好友信息可查看好友详细信息，顶部有搜索框，可根据用户名、邮箱、姓名模糊搜索好友。<br>
添加好友界面可根据用户名、邮箱查找用户，设置该好友所在组后发送好友申请，等待对方操作。
同时还有好友申请列表展示好友申请，可对申请进行通过和拒绝操作，通过则要先选择好友分组。
已发送的好友请求列表会展示已发送的未通过或等待验证的申请，同一个用户的好友申请不可发送多次，
若好友拒绝但此后通过搜索添加发送请求的用户，则无需验证可直接添加，列表可移除已发送的请求，移除后被请求好友再次添加请求的用户则需验证是否通过。<br>
可修改好友分组、删除好友，删除好友时被删除的用户好友列表的该用户的好友信息也会删除。<br>
  
![image](https://github.com/user-attachments/assets/3efe133d-3a70-418d-b1b4-83805ad1ea58)

</details>
<details>
<summary><h2>4、标签管理模块</h2></summary>
标签管理界面会显示所有标签数据，可进行编辑和删除，头部搜索框根据标签类型和标签名进行查找，添加标签和编辑标签时会判断标签名是否重复。<br>
  
![image](https://github.com/user-attachments/assets/8c01e21d-7efa-4f1a-8a11-666cb35815f5)

</details>
<details>
<summary><h2>5、文档管理模块</h2></summary>
我的文件界面顶部搜索框可根据标签类型、标签名、文件名进行文件搜索。
在我的文件界面顶部有创建文件夹和上传文件按钮用于对文档根部进行操作，对文件夹或文件右键进行创建文件夹和上传文件则是对该右键对象内部或同级目录进行操作。<br>
文件列表右方有按钮，点击可进行下载操作，下载路径为浏览器默认路径。<br>
鼠标双击文件可进行预览操作，支持图片、视频、doc、pdf、txt预览。<br>
文档编辑可编辑文档标签和文档的分享。
文件和文件夹可进行添加标签和好友分享操作，添加标签操作会展示所有已添加的标签，可对已添加的标签进行移除，下拉框含所有已创建的标签，添加已添加的标签会弹出错误提示；
好友分享操作有已分享的好友列表，可移除分享、修改分享时间，选择好友、设置分享时间区间则可添加分享好友，添加分享好友为已分享的好友会弹出错误提示。<br>
文件或文件夹可通过鼠标右键进行删除，文件夹的删除会遍历删除文件夹内所有文件。<br>
  
![image](https://github.com/user-attachments/assets/f7689570-0972-4b80-913b-0b9791ee0027)

</details>
