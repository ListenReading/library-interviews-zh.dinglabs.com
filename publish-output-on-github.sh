# Publish the contents of the /output folder to the gh-pages branch of library-interviews-zh.dinglabs.com
cd output
git init
git checkout -b gh-pages
git add .
git commit -m "web content"
git remote add origin git@github.com:ListenReading/library-interviews-zh.dinglabs.com.git
git push -u origin gh-pages -f
