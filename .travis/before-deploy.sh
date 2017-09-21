#!/usr/bin/env bash
if [ "$TRAVIS_PULL_REQUEST" == 'false' ] && [ ! -z "$TRAVIS_TAG" ]
then
    openssl aes-256-cbc -K $encrypted_24480018a79b_key -iv $encrypted_24480018a79b_iv -in .travis/codesigning.asc.enc -out .travis/codesigning.asc -d
    gpg --fast-import .travis/codesigning.asc
fi