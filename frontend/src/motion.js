// espelho dos tokens de movimento em src/index.css, em segundos para o motion
export const durations = {
  instant: 0.1,
  fast: 0.2,
  base: 0.32,
  slow: 0.56,
  slower: 0.9,
}

export const easings = {
  out: [0.22, 1, 0.36, 1],
  inOut: [0.65, 0, 0.35, 1],
  spring: [0.34, 1.56, 0.64, 1],
}

export const stagger = 0.06

// entradas ao fazer scroll, uma vez so e ja com o elemento bem dentro do ecra
export const aoVer = { once: true, amount: 0.35, margin: '0px 0px -20% 0px' }

export const revelar = {
  escondido: { opacity: 0, y: 64 },
  visivel: (atraso = 0) => ({
    opacity: 1,
    y: 0,
    transition: { duration: durations.slower, ease: easings.out, delay: atraso },
  }),
}
