const STORAGE_KEY = 'theme'

export function getTheme() {
  return document.documentElement.dataset.theme === 'light' ? 'light' : 'dark'
}

export function setTheme(theme) {
  document.documentElement.dataset.theme = theme
  try {
    localStorage.setItem(STORAGE_KEY, theme)
  } catch {
    // sem armazenamento, a escolha vale so para esta sessao
  }
}
