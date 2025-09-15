import masterImg from '../../../../assets/images/icons/stacked-files.svg';
import invoiceImg from '../../../../assets/images/icons/invoice.svg';
import reportImg from '../../../../assets/images/icons/import.svg';

export const getIconImg = imgClass => {
  let iconImgPath = '';
  
  switch (imgClass) {
      case 'master':
        iconImgPath = masterImg;
      break;

      case 'invoice':
        iconImgPath = invoiceImg;
      break;

      case 'report':
        iconImgPath = reportImg;
      break;

      default:
        iconImgPath = masterImg;
  }

  return iconImgPath;
};