#!/bin/sh
# entrypoint.sh

# We need to build the application after the spring boot backend starts up. 
# Build process of nextjs needs connection to backend and there is no way i know about
# that we can build the frontend after an other service has started in docker compose
# (That's why the entrypoint.sh is here)

npm run build

rm -rf node_modules

# https://nextjs.org/docs/app/api-reference/next-config-js/output#automatically-copying-traced-files


cp -r ./public ./.next/standalone/public
cp -r ./.next/static ./.next/standalone/.next/static

cp -r ./.next/standalone ../

# clean up
cd .. 
rm -r ./app

exec node ./standalone/server.js
