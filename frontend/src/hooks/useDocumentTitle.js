import { useEffect } from 'react';

export default function useDocumentTitle(title) {
  useEffect(() => {
    document.title = title ? `${title} | NEXORA` : 'NEXORA E-Commerce';
  }, [title]);
}
