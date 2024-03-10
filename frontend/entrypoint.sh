#!/bin/sh
# entrypoint.sh

# We need to build the application after the spring boot backend starts up. 
# Build process of nextjs needs connection to backend and there is no way i know about
# that we can build the frontend after an other service has started in docker compose
# (That's why the entrypoint.sh is here)

if [ -e "./standalone/server.js" ]; then
    echo "Server.js already exists, skipping build process."
else
    cd ./app
    npm run build

    # https://nextjs.org/docs/app/api-reference/next-config-js/output#automatically-copying-traced-files
    rm -rf node_modules

    cp -r ./public ./.next/standalone/public
    cp -r ./.next/static ./.next/standalone/.next/static

    cp -r ./.next/standalone ../

    cd ..
    rm -r ./app
fi

exec node ./standalone/server.js
