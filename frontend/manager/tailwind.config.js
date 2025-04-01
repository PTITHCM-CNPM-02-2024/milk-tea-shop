/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        'twitter-blue': '#1DA1F2',
        'mint-green': '#8CF5D2',
      },
    },
  },
  plugins: [],
}
