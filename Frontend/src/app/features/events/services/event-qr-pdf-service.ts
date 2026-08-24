import { Injectable } from '@angular/core';
import { jsPDF } from 'jspdf';

import { EventCodesResponse } from '@features/events/model/event-codes-response';

export type EventQrPdfData = {
  id: number;
  codes: EventCodesResponse;
  eventName?: string;
  startDateTime?: string;
  poster?: string;
};

@Injectable({ providedIn: 'root' })
export class EventQrPdfService {
  async download({ id, codes, eventName, startDateTime, poster }: EventQrPdfData): Promise<void> {
    const doc = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: 'a4',
    });

    const pageWidth = 210;
    const pageHeight = 297;
    const margin = 14;
    const centerX = pageWidth / 2;

    let currentY = 20;

    if (poster) {
      const mime = poster.startsWith('iVBORw0KGgo') ? 'image/png' : 'image/jpeg';
      const imgSrc = `data:${mime};base64,${poster}`;
      const { width, height } = await this.getImageDimensions(imgSrc);
      const bannerH = Math.min(70, (height / width) * pageWidth);

      doc.addImage(imgSrc, mime === 'image/png' ? 'PNG' : 'JPEG', 0, 0, pageWidth, bannerH);

      currentY = bannerH + 10;
    }

    if (eventName) {
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(22);
      doc.setTextColor(20, 20, 20);

      const nameLines = doc.splitTextToSize(eventName, pageWidth - margin * 2);

      doc.text(nameLines, centerX, currentY, { align: 'center' });

      currentY += nameLines.length * 9 + 4;
    }

    if (startDateTime) {
      const dateStr = new Date(startDateTime).toLocaleDateString('en-US', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric',
      });

      doc.setFont('helvetica', 'bold');
      doc.setFontSize(12);
      doc.setTextColor(100, 100, 100);

      doc.text(dateStr, centerX, currentY, { align: 'center' });

      currentY += 7;
    }

    currentY += 8;

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(18);
    doc.setTextColor(20, 20, 20);

    const scanTextY = currentY;

    doc.text('Please scan the QR code', centerX, scanTextY, { align: 'center' });

    const trimmedQr = await this.trimQrWhitespace(codes.qrCode);

    const qrSize = 105;
    const qrY = scanTextY + 7 + 2;

    doc.addImage(trimmedQr, 'PNG', (pageWidth - qrSize) / 2, qrY, qrSize, qrSize);

    currentY = qrY + qrSize + 15;

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(18);
    doc.setTextColor(20, 20, 20);

    doc.text('OR', centerX, currentY, { align: 'center' });

    currentY += 12;

    doc.text('Enter the following code', centerX, currentY, { align: 'center' });

    currentY += 18;

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(27);
    doc.setTextColor(20, 20, 20);

    doc.text(codes.checkInCode, centerX, currentY, { align: 'center' });

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(9);
    doc.setTextColor(160, 160, 160);

    doc.text('Powered by msg Check-In', centerX, pageHeight - 8, { align: 'center' });

    doc.save(`check-in-qr-${id}.pdf`);
  }

  private trimQrWhitespace(base64: string): Promise<string> {
    return new Promise((resolve) => {
      const img = new Image();

      img.onload = () => {
        const canvas = document.createElement('canvas');

        canvas.width = img.naturalWidth;
        canvas.height = img.naturalHeight;

        const ctx = canvas.getContext('2d', {
          willReadFrequently: true,
        });

        if (!ctx) {
          resolve(`data:image/png;base64,${base64}`);
          return;
        }

        ctx.drawImage(img, 0, 0);

        const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);

        const { data, width, height } = imageData;

        let minX = width;
        let minY = height;
        let maxX = -1;
        let maxY = -1;

        for (let y = 0; y < height; y++) {
          for (let x = 0; x < width; x++) {
            const index = (y * width + x) * 4;

            const r = data[index];
            const g = data[index + 1];
            const b = data[index + 2];
            const alpha = data[index + 3];

            const isQrPixel = alpha > 0 && (r < 245 || g < 245 || b < 245);

            if (isQrPixel) {
              minX = Math.min(minX, x);

              minY = Math.min(minY, y);

              maxX = Math.max(maxX, x);

              maxY = Math.max(maxY, y);
            }
          }
        }

        if (maxX === -1) {
          resolve(`data:image/png;base64,${base64}`);
          return;
        }

        // 2% quiet-zone required by QR spec
        const padding = Math.max(2, Math.round(Math.min(width, height) * 0.02));

        minX = Math.max(0, minX - padding);

        minY = Math.max(0, minY - padding);

        maxX = Math.min(width - 1, maxX + padding);

        maxY = Math.min(height - 1, maxY + padding);

        const cropWidth = maxX - minX + 1;

        const cropHeight = maxY - minY + 1;

        const size = Math.max(cropWidth, cropHeight);

        const outputCanvas = document.createElement('canvas');

        outputCanvas.width = size;
        outputCanvas.height = size;

        const outputCtx = outputCanvas.getContext('2d');

        if (!outputCtx) {
          resolve(`data:image/png;base64,${base64}`);
          return;
        }

        outputCtx.fillStyle = '#ffffff';

        outputCtx.fillRect(0, 0, size, size);

        const offsetX = (size - cropWidth) / 2;

        const offsetY = (size - cropHeight) / 2;

        outputCtx.drawImage(
          canvas,
          minX,
          minY,
          cropWidth,
          cropHeight,
          offsetX,
          offsetY,
          cropWidth,
          cropHeight
        );

        resolve(outputCanvas.toDataURL('image/png'));
      };

      img.onerror = () => {
        resolve(`data:image/png;base64,${base64}`);
      };

      img.src = `data:image/png;base64,${base64}`;
    });
  }

  private getImageDimensions(src: string): Promise<{ width: number; height: number }> {
    return new Promise((resolve) => {
      const img = new Image();

      img.onload = () => {
        resolve({
          width: img.naturalWidth,
          height: img.naturalHeight,
        });
      };

      img.onerror = () => {
        resolve({
          width: 16,
          height: 9,
        });
      };

      img.src = src;
    });
  }
}
