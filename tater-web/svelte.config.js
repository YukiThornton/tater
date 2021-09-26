const sveltePreprocess = require('svelte-preprocess');
const path = require('path');

// using sourceMap as an example, but could be anything you need dynamically
function createPreprocessors(sourceMap) {
    return sveltePreprocess({
        sourceMap,
        scss: {
            prependData: `@import '${path.join(__dirname, 'src/styles/variables.scss')}';`,
            includePaths: ['src', 'node_modules']
        }
    });
}

module.exports = {
    preprocess: createPreprocessors(true),
    createPreprocessors
};