import {Alert} from 'react-native';


 class Dialog{
    alert(title:string, message:string){
        Alert.alert(
            title, message, 
            [
                {
                    text: 'OK', 
                    onPress: () => console.log('OK Pressed'),
                    style: 'cancel'
                }
            ]
        );
    }
    
    confirm(title:string, message:string, positiveCallback:Function, negativeCallback:Function ){
        Alert.alert(
            title, message, 
            [
                {
                    text: 'Yes', 
                    onPress: () => positiveCallback(),
                    style: 'cancel'
                },

                {
                    text: 'No', 
                    onPress: () => negativeCallback(),
                    style: 'cancel'
                }
            ]
        );
    }
}

export const dialog = new Dialog();