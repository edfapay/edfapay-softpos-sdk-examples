import { StyleSheet, Dimensions} from 'react-native';

const screen = Dimensions.get('window');
export const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  content: {
    alignItems: 'center',
  },
  logo: {
    width: screen.width/1.5,
    resizeMode: 'contain',
  },
  heading1: {
    fontSize: 65,
    fontWeight: "700",
    color: "#000",
  },
  heading2: {
    fontSize: 30,
    fontWeight: "700",
    color: "#000",
  },
  heading3: {
    marginHorizontal:30,
    fontSize: 13,
    fontWeight: "400",
    color: "#787878",
    marginVertical: 100,
  },
  buttonContainer: {
    position: 'absolute',
    bottom: 20,
    left: 0,
    right: 0,
    marginHorizontal: 20,
    borderRadius: 10,
    overflow: 'hidden',
  },
});