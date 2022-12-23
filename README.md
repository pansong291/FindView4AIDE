# FindView4AIDE
[![License](https://img.shields.io/github/license/pansong291/FindView4AIDE.svg)](LICENSE)
[![Latest Release](https://img.shields.io/github/release/pansong291/FindView4AIDE.svg)](../../releases)
[![All Releases Download](https://img.shields.io/github/downloads/pansong291/FindView4AIDE/total.svg)](../../releases)

A xposed mudule to complete "findViewById" code for AIDE.

### 如何使用
首先，这是一个Xposed模块，你必须具备关于Xposed的基本常识，不懂请百度。  
目前，该模块只支持 AIDE v3.2.190809 版本。  

打开xml布局代码，长按选中需要findViewById的代码（或者直接全选也可以）。  
![选择xml代码](https://pansong291.github.io/Pictures/github/pansong291/FindView4AIDE/FindView4AIDE_help_select_content.jpg)

点击右上角菜单，选择"FindViewById"，如果没有，那么可能是你没有长按选代码，也可能是模块未激活。  
![点击菜单](https://pansong291.github.io/Pictures/github/pansong291/FindView4AIDE/FindView4AIDE_help_click_menuItem.jpg)

弹出对话框，根据自己需求打勾，确定后会直接复制到剪切板。  
![配置需求](https://pansong291.github.io/Pictures/github/pansong291/FindView4AIDE/FindView4AIDE_help_configure.jpg)

在合适的位置粘贴代码，结束。  
![粘贴java代码](https://pansong291.github.io/Pictures/github/pansong291/FindView4AIDE/FindView4AIDE_help_paste.jpg)
