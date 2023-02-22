import React, { useState, useEffect } from 'react';
import { TextField, Button, Typography, Paper } from '@material-ui/core';
import { useDispatch, useSelector } from 'react-redux';

import { createPost, updatePost } from '../../actions/posts';
import useStyles from './styles';

const Form = ({ currentId, setCurrentId }) => {
  const [postData, setPostData] = useState({ productName: '', price: '', quantity: '' });
  const post = useSelector((state) => (currentId ? state.posts.find((message) => message.id === currentId) : null));
  const dispatch = useDispatch();
  const classes = useStyles();
  const user = JSON.parse(localStorage.getItem('profile'));

  useEffect(() => {
    if (post) setPostData(post);
  }, [post]);

  const clear = () => {
    setCurrentId(0);
    setPostData({ productName: '', price: '', quantity: '' });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (currentId === 0) {
      dispatch(createPost({ ...postData, name: user?.result?.email }));
      clear();
    } else {
      dispatch(updatePost(currentId, { ...postData, name: user?.result?.email }));
      clear();
    }
  };

  if (!user?.username) {
    return (
      <Paper className={classes.paper}>
        <Typography variant="h6" align="center">
          Please Sign In.
        </Typography>
      </Paper>
    );
  }

  return (
    <Paper className={classes.paper}>
      {
        user.roles[0] === "ADMIN" ? (
          <form autoComplete="off" noValidate className={`${classes.root} ${classes.form}`} onSubmit={handleSubmit}>
        <Typography variant="h6">{currentId ? `Editing "${post.title}"` : 'Creating a Product'}</Typography>
        <TextField name="productName" variant="outlined" label="Product Name" fullWidth value={postData.productName} onChange={(e) => setPostData({ ...postData, productName: e.target.value })} />
        <TextField name="price" variant="outlined" label="Price" fullWidth value={postData.price} onChange={(e) => setPostData({ ...postData, price: e.target.value })} />
        <TextField name="quantity" variant="outlined" label="Quantity" fullWidth value={postData.quantity} onChange={(e) => setPostData({ ...postData, quantity: e.target.value })} />
        <Button className={classes.buttonSubmit} variant="contained" color="primary" size="large" type="submit" fullWidth>Submit</Button>
        <Button variant="contained" color="secondary" size="small" onClick={clear} fullWidth>Clear</Button>
      </form>
        ) : (
          <Typography variant="h6" align="center">
            You don't have access to create post.
           </Typography>
        )
      }
     
    </Paper>
  );
};

export default Form;
